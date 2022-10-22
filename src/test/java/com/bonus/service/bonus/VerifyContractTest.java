package com.bonus.service.bonus;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.target.TestTarget;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.bonus.service.bonus.configuration.BonusConfiguration;
import com.bonus.service.bonus.controller.BonusController;
import com.bonus.service.common.entity.Bonus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.sql.Date;
import java.util.List;

@VerificationReports
@SpringBootTest(classes = TestConfiguration.class)
@Provider(value = "bonus-service")
@PactFolder("src/test/resources/pacts")
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
        testTarget.setServletPath("/bonus");
        testTarget.setPrintRequestResponse(true);
        context.setTarget(testTarget);
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context, MockHttpServletRequestBuilder request) {
        request.header("Content-Type", "application/json");
        context.verifyInteraction();
    }

    @State("I get bonus")
    void getAllBonuses(){
        Bonus bonus = Bonus.builder()
                .id(1)
                .bonusName("MEGA")
                .bonusType("MEGA_BONUS")
                .startTime(Date.valueOf("2019-10-10"))
                .endTime(Date.valueOf("2022-10-10")).build();
        BDDMockito.doReturn(List.of(bonus)).when(bonusController).getBonuses();
    }
}
