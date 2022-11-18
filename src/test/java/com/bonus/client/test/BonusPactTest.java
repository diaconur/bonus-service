package com.bonus.client.test;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
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
@PactTestFor(providerName = "bonus-service")
@Slf4j
public class BonusPactTest {

    @Pact(consumer = "bonus-consumer")
    public V4Pact getBonus(PactDslWithProvider builder) throws Exception {

        //Method1 using DslPart from pact
        DslPart bonusResponse1 = new PactDslJsonBody()
                .maxArrayLike("bonus", 1)
                .integerType("id", 1)
                .stringType("bonusName", "SUPER")
                .stringType("bonusType", "MEGA_BONUS")
                .stringType("startTime", "2019-10-10")
                .stringType("endTime", "2019-10-10").closeObject();

        //Method2 Reading from json
        String bonusResponse2 = readFileAsString(System.getProperty("user.dir") + "/src/test/resources/bonus.json");
        log.info("Bonus is {}", bonusResponse2);


        return builder.given("I get all bonuses")
                .uponReceiving("Get bonus call")
                .path("/bonus/getAllBonuses")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(bonusResponse2)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getBonus", port = "8081")
    public void testGetBonuses(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

        ResponseEntity<String> response = restTemplate.getForEntity("/bonus/getAllBonuses", String.class);
        log.info("Response body contains{}", response.getBody());
        int statusCode = restTemplate.getForEntity("/bonus/getAllBonuses", String.class).getStatusCode().value();
        Assertions.assertEquals(statusCode, 200);
    }

    public String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}