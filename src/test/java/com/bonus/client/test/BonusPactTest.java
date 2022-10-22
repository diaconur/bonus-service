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
import com.pact.consumer.dto.BonusResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "bonus-service")
@Slf4j
public class BonusPactTest {

    @Pact(consumer = "bonus-consumer")
    public V4Pact getBonus(PactDslWithProvider builder) throws IOException {

    //        File bonusFile = new
    // File(this.getClass().getClassLoader().getResource("bonus.json").getFile());
    //        ObjectMapper mapper = new ObjectMapper();
    //        BonusResponse response = mapper.readValue(bonusFile,BonusResponse.class);

        BonusResponse responseObj =
               new ObjectMapper()
                        .readValue(
                                ResourceUtils
                                        .getFile(
                                                "classpath:src/resources/bonus.json"),
                                BonusResponse.class);
    DslPart response = new PactDslJsonBody()
            .maxArrayLike("test" ,1)
            .integerType("id", 1)
            .stringType("bonusName", "MEGA")
            .stringType("bonusType", "MEGA_BONUS")
            .stringType("startTime", "2019-10-10")
            .stringType("endTime", "2019-10-10").closeObject();

        log.info("Response contains {}", response.toString());
        return builder.given("I get bonus")
                .uponReceiving("Get bonus call")
                .path("/bonus/getAllBonuses")
                .method("GET")
//                .headers("Content-Type", "application/json")
                .willRespondWith()
                .status(200)
                .body(response)
                .toPact(V4Pact.class);

    }

    @Test
    @PactTestFor(pactMethod = "getBonus", port = "8081")
    public void testGetBonuses(MockServer mockServer){
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        String body = restTemplate.getForEntity("/bonus/getAllBonuses", String.class).getBody();
        int statusCode = restTemplate.getForEntity("/bonus/getAllBonuses", String.class).getStatusCode().value();
        Assertions.assertEquals(statusCode, 200);
    }
}
