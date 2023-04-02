package com.github.dergil.elasticdemo.car;

import com.github.dergil.elasticdemo.car.domain.dto.calculate.CalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarCalculateView;
import com.github.dergil.elasticdemo.car.domain.dto.car.CarView;
import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// start docker-compose.yml file beforehand, and wait at least 30 seconds
@Slf4j
public class PlatformTests {
    public static void main(String[] args) throws InterruptedException {
        try {
            logAllIndices();
            testCarToElasticsearch();
            testCalculatorToElasticsearch();
            testGatewayToCarToElasticsearch();
            testGatewayToCarToCalculatorToElasticsearch();
            logAllIndices();
        } finally {
            removeAllIndices();
        }

    }
    public static RestTemplate restTemplate = new RestTemplate();

    private static CarView createExampleCar(String port, String carName) {
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

    private static CalculateView createExampleCalculateRequest(String port, double price, double salesTax) {
        String apiEndpointUrl = "http://localhost:" + port + "/calculate?price=" +
                Double.toString(price) + "&salesTax=" + Double.toString(salesTax);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(apiEndpointUrl, HttpMethod.GET, requestEntity, CalculateView.class).getBody();
    }

    private static CarCalculateView getTaxForCar(String port, long id, double tax) {
        String apiEndpointUrl = "http://localhost:" + port + "/car/tax/" + id + "?tax=" + tax;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(apiEndpointUrl, HttpMethod.GET, requestEntity, CarCalculateView.class).getBody();
    }


    static void testCarToElasticsearch() throws InterruptedException {
        String exampleCarName = "car_test_two";
        createExampleCar("8081", exampleCarName);
        String indexName = findMatchingIndexName("car");
        assertIndexContainsString(exampleCarName, indexName);
//        getAllIndices();
    }

    static void testCalculatorToElasticsearch() throws InterruptedException {
        double salesTax = 0.1955;
        createExampleCalculateRequest("8084", 99999.0, salesTax);
        String indexName = findMatchingIndexName("calculator");
        assertIndexContainsString(Double.toString(salesTax), indexName);
    }

    static void testGatewayToCarToCalculatorToElasticsearch() throws InterruptedException {
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
    }

    private static String findMatchingIndexName(String indexName) {
        return getAllIndices().stream()
//                todo
                .filter(index -> index.contains(indexName) && index.contains(getCurrentDateInCustomFormat()))
//                .filter(index -> index.contains(indexName) && index.contains("2023.04.01"))
                .findFirst().get();
    }

    static void assertIndexContainsString(String string, String index) throws InterruptedException {
//        getAllIndices();
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
            log.info(response.getBody());
            System.err.println("###########");
            if (Objects.requireNonNull(response.getBody()).contains(string)) {
                break;
            }
        }
        log.info("Number of iterations for search: " + currentIteration);
        log.info(response.getBody());
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains(string));
    }

    static void testGatewayToCarToElasticsearch() throws InterruptedException {
        String exampleCarName = "car23";
        String gatewayMatchString = "POST http://localhost:8080/car";
        createExampleCar("8080", exampleCarName);
        String indexNameGateway = findMatchingIndexName("gateway");
        String indexNameCar = findMatchingIndexName("car");
//        testIndexContainsString(exampleCarName, "docker-elk-gateway-1"+ "-" + getCurrentDateInCustomFormat());
        assertIndexContainsString(gatewayMatchString, indexNameGateway);
        assertIndexContainsString(exampleCarName, indexNameCar);
    }

    private static void removeAllIndices() {
        for (String index : getAllIndices()) {
            removeAllIndicesWithSubstring(index);
        }
    }

    private static void removeAllIndicesWithSubstring(String index) {
        String elasticUrl = "http://localhost:9200/" + index;
        HttpEntity httpEntity = new HttpEntity(null);
        ResponseEntity<String> responseMS  = restTemplate.exchange(elasticUrl, HttpMethod.DELETE, httpEntity, String.class);
        Assertions.assertEquals(responseMS.getStatusCode(), HttpStatus.OK);
    }

    public static String getCurrentDateInCustomFormat() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return currentDate.format(formatter);
    }

    private static void logAllIndices() {
        log.info(getAllIndices().toString());
    }

    private static List<String> getAllIndices() {
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
}
