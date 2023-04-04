package com.github.dergil.elasticdemo.car;

import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarCalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarView;
import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Testcontainers
public class TestcontainersPlatformTest {
    boolean init = false;
    String elasticSearchFinished = "GREEN";
    String filebeatFinished = "established";
    String logstashFinished = "Pipelines running";

    public  RestTemplate restTemplate = new RestTemplate();

    @Container
    public  DockerComposeContainer<?> environment = new DockerComposeContainer<>(new File("../docker-compose.yml"))
            .withExposedService("elasticsearch", 9200)
            .withExposedService("logstash", 5044)
            .withExposedService("car", 8081)
            .withExposedService("calculator", 8084)
            .withExposedService("gateway", 8080);

    @BeforeEach
    void setup() {
        if (!init) {
            environment.withLogConsumer("elasticsearch", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.withLogConsumer("car", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.withLogConsumer("logstash", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.withLogConsumer("filebeat", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.waitingFor("elasticsearch", Wait.forLogMessage(".*" + elasticSearchFinished + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            environment.waitingFor("logstash", Wait.forLogMessage(".*" + logstashFinished + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            environment.waitingFor("filebeat", Wait.forLogMessage(".*" + filebeatFinished + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            log.debug("Started ELK stack");
            environment.waitingFor("car", Wait.forLogMessage(".*" + "Started CarApplication" + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            log.debug("Started car service");
            environment.start();
            init = true;
        }
    }

    @AfterEach
    void tearDown() {
        removeAllIndices();
        log.debug("All indices removed");
    }

    @Test
    void testCarToElasticsearch() throws InterruptedException {
        String exampleCarName = "car_test_two";
        createExampleCar("8081", exampleCarName);
        String indexName = findMatchingIndexName("car");
        assertIndexContainsString(exampleCarName, indexName);
    }

    @Test
    void testCalculatorToElasticsearch() throws InterruptedException {
        double salesTax = 0.1955;
        createExampleCalculateRequest("8084", 99999.0, salesTax);
        String indexName = findMatchingIndexName("calculator");
        assertIndexContainsString(Double.toString(salesTax), indexName);
    }

    @Test
    void testGatewayToCarToElasticsearch() throws InterruptedException {
        String exampleCarName = "car23";
        String gatewayMatchString = "POST http://localhost:8080/car";
        createExampleCar("8080", exampleCarName);
        String indexNameGateway = findMatchingIndexName("gateway");
        String indexNameCar = findMatchingIndexName("car");
        assertIndexContainsString(gatewayMatchString, indexNameGateway);
        assertIndexContainsString(exampleCarName, indexNameCar);
    }

    @Test
    void testGatewayToCarToCalculatorToElasticsearch() throws InterruptedException {
        String carName = "villCar";
        CarView carView = createExampleCar("8080", carName);
        double salesTax = 0.1355;
        CarCalculateView carCalculateView = getTaxForCar("8080", carView.getId(), salesTax);
        String carIndex = findMatchingIndexName("car");
        String gatewayIndex = findMatchingIndexName("gateway");
        String calculatorIndex = findMatchingIndexName("calculator");
        assertIndexContainsString(Double.toString(salesTax), calculatorIndex);
        assertIndexContainsString(Double.toString(salesTax), carIndex);
        assertIndexContainsString(carName, carIndex);
        assertIndexContainsString("/" + Long.toString(carCalculateView.getCarView().getId()), gatewayIndex);
    }

    private  String findMatchingIndexName(String indexName) {
        return getAllIndices().stream()
                .filter(index -> index.contains(indexName) && index.contains(getCurrentDateInCustomFormat()))
                .findFirst().get();
    }

    void assertIndexContainsString(String string, String index) throws InterruptedException {
//        wait for filebeat, logstash and elasticsearch to process request log
        int maxIterations = 5;
        int currentIteration = 0;

        ResponseEntity<String> response = null;

        while (currentIteration < maxIterations) {
            Thread.sleep(5000);
            currentIteration++;
            String fooResourceUrl
                    = "http://localhost:9200/" + index + "/_search?size=10000&from=0";
            response = restTemplate.getForEntity(fooResourceUrl, String.class);
            log.debug(response.getBody());
            log.debug("###########");
            if (Objects.requireNonNull(response.getBody()).contains(string)) {
                break;
            }
        }
        log.info("Number of iterations for search: " + currentIteration);
        log.debug(response.getBody());
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains(string));
    }

    private void removeAllIndices() {
        for (String index : getAllIndices()) {
            removeAllIndicesWithSubstring(index);
        }
    }

    private  void removeAllIndicesWithSubstring(String index) {
        String elasticUrl = "http://localhost:9200/" + index;
        HttpEntity httpEntity = new HttpEntity(null);
        ResponseEntity<String> responseMS  = restTemplate.exchange(elasticUrl, HttpMethod.DELETE, httpEntity, String.class);
        Assertions.assertEquals(responseMS.getStatusCode(), HttpStatus.OK);
    }

    public  String getCurrentDateInCustomFormat() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return currentDate.format(formatter);
    }

    private  List<String> getAllIndices() {
        String baseUrl = "http://localhost:9200";
        String endpoint = "/_cat/indices";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl).path(endpoint).build().toUri();

        WebClient webClient = WebClient.create();

        String responseBody = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.trace(responseBody);
        List<String> indexNames = new ArrayList<>();
        if (responseBody == null){
            return new ArrayList<>();
        }
        String[] lines = responseBody.split("\n");
        for (String line : lines) {
            indexNames.add(line.split(" ")[2]);
        }
        return indexNames;
    }

    private  CarView createExampleCar(String port, String carName) {
        EditCarRequest newCar = new EditCarRequest();
        newCar.setName(carName);
        newCar.setPrice(1000.0);
        newCar.setMilesPerGallon(20.0F);
        newCar.setCylinders(4);
        newCar.setDisplacement(2000);
        newCar.setHorsepower(150);
        newCar.setWeightInPounds(2500);
        newCar.setAcceleration(10.0F);
        newCar.setYear(new Date());
        newCar.setOrigin("example origin");

        String apiEndpointUrl = "http://localhost:" + port + "/car";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EditCarRequest> requestEntity = new HttpEntity<>(newCar, headers);
        return restTemplate.postForObject(apiEndpointUrl, requestEntity, CarView.class);
    }

    private void createExampleCalculateRequest(String port, double price, double salesTax) {
        String apiEndpointUrl = "http://localhost:" + port + "/calculate?price=" +
                Double.toString(price) + "&salesTax=" + Double.toString(salesTax);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity requestEntity = new HttpEntity<>(null, headers);
        restTemplate.exchange(apiEndpointUrl, HttpMethod.GET, requestEntity, CalculateView.class).getBody();
    }

    private  CarCalculateView getTaxForCar(String port, long id, double tax) {
        String apiEndpointUrl = "http://localhost:" + port + "/car/tax/" + id + "?tax=" + tax;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(apiEndpointUrl, HttpMethod.GET, requestEntity, CarCalculateView.class).getBody();
    }
}




