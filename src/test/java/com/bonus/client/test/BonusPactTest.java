package com.bonus.client.test;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.bonus.client.test.dto.BonusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "bonus-service")
@Slf4j
public class BonusPactTest {

    @Pact(consumer = "bonus-consumer")
    public V4Pact getBonus(PactDslWithProvider builder) throws Exception {

        //Method1 using DslPart from pact
        DslPart bonusResponse1 = new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("bonusName", "SUPER")
                .stringType("bonusType", "MEGA_BONUS")
                .stringMatcher("startTime", "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$","2019-10-22")
                .stringMatcher("endTime","^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", "2019-10-30");

//      Method2 Reading from json
        String bonusResponse2 = readFileAsString(System.getProperty("user.dir") + "/src/test/resources/bonus.json");
        log.info("Bonus is {}", bonusResponse2);


        return builder.given("I get all bonuses")
                .uponReceiving("Get bonus call")
                .path("/bonus/getAllBonuses")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body("["+bonusResponse1+"]")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getBonus", port = "8081")
    public void testGetBonuses(MockServer mockServer) throws JSONException {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

        ResponseEntity<String> response = restTemplate.getForEntity("/bonus/getAllBonuses", String.class);
        log.info("Response body contains{}", response.getBody());
        JSONArray array = new JSONArray(response.getBody());
        JSONObject bonus = new JSONObject(array.get(0).toString());

        int statusCode = response.getStatusCode().value();
        Assertions.assertEquals(statusCode, 200);
        Assertions.assertEquals(bonus.get("bonusType"), "MEGA_BONUS");
    }

    public String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}