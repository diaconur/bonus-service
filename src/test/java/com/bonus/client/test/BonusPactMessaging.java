package com.bonus.client.test;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "bonus-service", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
public class BonusPactMessaging {

    @Pact(provider = "bonus-service", consumer = "bonus-consumer")
    public MessagePact getBonusMessage(MessagePactBuilder builder) {
        DslPart bonusResponse1 = new PactDslJsonBody()
                .maxArrayLike("bonus", 1)
                .integerType("id", 1)
                .stringType("bonusName", "SUPER")
                .stringType("bonusType", "MEGA_BONUS")
                .stringType("startTime", "2019-10-10")
                .stringType("endTime", "2019-10-10").closeObject();

        return builder.expectsToReceive("I receive a bonus")
                .withMetadata(Map.of("contentType", "application/json"))
                .withContent(bonusResponse1).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getBonusMessage", providerType = ProviderType.ASYNCH)
    public void testGetBonusMessage(List<Message> message){
        log.info("Message content type {}", message.get(0).getContents().toV4Format().get("content"));
    }
}
