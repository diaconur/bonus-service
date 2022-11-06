package com.bonus.client.test;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PROVIDER_SERVICE")
@Slf4j
public class PactClassTemplate {

    @Pact(consumer = "CONSUMER_SERVICE")
    public V4Pact PACT_METHOD(PactDslWithProvider builder) throws Exception {

        //Method2 Reading from json
        String response = readFileAsString(System.getProperty("user.dir") + "/src/test/resources/RESPONSE_JSON");
        log.info("Response is {}", response);


        return builder.given("STATE")
                .uponReceiving("DESCRIPTION")
                .path("API_PATH")
                .method("API_METHOD")
                .willRespondWith()
                .status(200)
                .body(response)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "PACT_METHOD", port = "8081")
    public void testPACT_METHOD(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        ResponseEntity<String> response = restTemplate.getForEntity("API_PATH", String.class);
        log.info("Response body contains{}", response.getBody());
        Assertions.assertEquals(response.getStatusCode().value(), 200);
    }

    public String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}