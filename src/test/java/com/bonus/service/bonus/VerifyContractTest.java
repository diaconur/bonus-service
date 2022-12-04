package com.bonus.service.bonus;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.bonus.service.bonus.controller.BonusController;
import com.bonus.service.common.entity.Bonus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@VerificationReports
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,classes = TestConfiguration.class)
@Provider(value = "bonus-service")
//@PactFolder("pacts")
@Slf4j
@PactBroker
public class VerifyContractTest {

    @MockBean
    BonusController bonusController;

    @TestTarget
    public MockMvcTestTarget testTarget = new MockMvcTestTarget();

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    @BeforeEach
    void setup(PactVerificationContext context){
        testTarget.setControllers(bonusController);
        testTarget.setMessageConverters(converter);
        testTarget.setPrintRequestResponse(true);
        context.setTarget(testTarget);
//        System.setProperty("pact.verifier.publishResults", "true");
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context, MockHttpServletRequestBuilder request) {
        request.header("Content-Type", "application/json");
        context.verifyInteraction();
    }

    @State("I get all bonuses")
    void getAllBonuses() throws IOException {

        // Method1 using a builder
        Bonus bonus = Bonus.builder()
                .id(1)
                .bonusName("MEGA")
                .bonusType("MEGA_BONUS")
                .startTime(Date.valueOf("2019-10-10"))
                .endTime(Date.valueOf("2019-10-10")).build();

        // Method2 Reading from json file
        File myRequest = ResourceUtils.getFile("classpath:requests/bonusRequest.json");
        Bonus bonusRequest = new ObjectMapper().readValue(myRequest, Bonus.class);
        log.info("Bonus is {}", bonusRequest.toString());

        BDDMockito.doReturn(List.of(bonus)).when(bonusController).getBonuses();
    }
}
