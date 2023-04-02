package com.github.dergil.elasticdemo.car;

import com.github.dergil.elasticdemo.car.domain.dto.car.EditCarRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
// commented, so that mvn does not try to execute, so that the test env does not get built
@Testcontainers
public class TestcontainersPlatformTest {
//    not working, see thesis
    boolean init = false;
    String elasticSearchFinished = "GREEN";
    String filebeatFinished = "established";
    String logstashFinished = "Pipelines running";

    @Container
    public static DockerComposeContainer<?> environment = new DockerComposeContainer<>(new File("../docker-compose.yml"))
            .withExposedService("elasticsearch", 9200)
            .withExposedService("logstash", 5044)
            .withExposedService("kibana", 5601)
            .withExposedService("car", 8081)
            .withExposedService("calculator", 8084)
            .withExposedService("gateway", 8080);

    @BeforeEach
    void setup() throws InterruptedException {
        if (!init) {
            environment.withLogConsumer("elasticsearch", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.withLogConsumer("car", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.withLogConsumer("logstash", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.withLogConsumer("filebeat", new Slf4jLogConsumer(TestcontainersPlatformTest.log));
            environment.waitingFor("elasticsearch", Wait.forLogMessage(".*" + elasticSearchFinished + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            environment.waitingFor("logstash", Wait.forLogMessage(".*" + logstashFinished + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            environment.waitingFor("filebeat", Wait.forLogMessage(".*" + filebeatFinished + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            System.err.println("Started ELK stack");
            environment.waitingFor("car", Wait.forLogMessage(".*" + "Started CarApplication" + ".*", 1).withStartupTimeout(Duration.ofSeconds(160)));
            environment.start();
            init = true;
        }
    }

//    @Disabled
    @Test
    void testInteraction() throws InterruptedException {
        log.info("hro do weisst");

        RestTemplate restTemplate = new RestTemplate();

        String carName = "example name";
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

        String apiEndpointUrl = "http://localhost:8081/car";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EditCarRequest> requestEntity = new HttpEntity<>(newCar, headers);
        String createdCar = restTemplate.postForObject(apiEndpointUrl, requestEntity, String.class);

        // Wait for the logs to be ingested into Elasticsearch
        Thread.sleep(10000);

        org.testcontainers.containers.ContainerState containerState = (ContainerState) environment.getContainerByServiceName("car_1").get();
        System.err.println(containerState.getContainerInfo().getName());
        String containerName = containerState.getContainerInfo().getName().replace("/", "");
        String indexName = containerName + "-" + getCurrentDateInCustomFormat();

        getAllIndices();

        String fooResourceUrl
                = "http://localhost:9200/" + indexName + "/_search?size=10000&from=0";
        ResponseEntity<String> response2
                = restTemplate.getForEntity(fooResourceUrl, String.class);
        System.err.println(response2.getBody());
        Assertions.assertEquals(response2.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(Objects.requireNonNull(response2.getBody()).contains(carName));
    }

    public String getCurrentDateInCustomFormat() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return currentDate.format(formatter);
    }

    private String extractIndexName(String jsonInput, String partialIndexName) {
        String regex = "\"index\":\"(.*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonInput);

        while (matcher.find()) {
             String indexValue = matcher.group(1);
             System.err.println(indexValue);
            if (indexValue.contains(partialIndexName)) {
                System.out.println(indexValue);
                System.err.println("index found: " + indexValue);
                return indexValue;
            }
        }
        System.err.println("index not found");
        return null;
    }

    private String getAllIndices() {
        RestTemplate restTemplate = new RestTemplate();

        String elasticsearchUrl = "http://localhost:9200/_cat/indices?h=index";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                elasticsearchUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
        System.err.println("All indices: " + response.getBody());

        return response.getBody();
    }

}




