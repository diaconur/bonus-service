package com.bonus.client.test;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PROVIDER-SERVICE")
@Slf4j
public class BonusPactTestTemplate {

    @Pact(consumer = "CONSUMER-SERVICE")
    public V4Pact getBonus(PactDslWithProvider builder) throws IOException {

    DslPart response = new PactDslJsonBody()
            .maxArrayLike("bonus" ,1)
            .integerType("id", 1)
            .stringType("bonusName", "SUPER")
            .stringType("bonusType", "MEGA_BONUS")
            .stringType("startTime", "2019-10-10")
            .stringType("endTime", "2019-10-10").closeObject();

        File myRequest = ResourceUtils.getFile("classpath:requests/bonusRequest.json");
        String bonusRequest = new ObjectMapper().readValue(myRequest, String.class);
        log.info("Bonus is {}", bonusRequest);

        log.info("Response contains {}", response.toString());
        return builder.given("I get all bonuses")
                .uponReceiving("Get bonus call")
                .path("/bonus/getAllBonuses")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(response.toString())
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getBonus", port = "8081")
    public void testGetBonuses(MockServer mockServer){
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        String body = restTemplate.getForEntity("/bonus/getAllBonuses", String.class).getBody();
        log.info("Response body contains{}", body);
        int statusCode = restTemplate.getForEntity("/bonus/getAllBonuses", String.class).getStatusCode().value();
        Assertions.assertEquals(statusCode, 200);
    }
}
