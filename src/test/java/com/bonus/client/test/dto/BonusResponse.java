package com.bonus.client.test.dto;

import lombok.Builder;
import lombok.Value;

import java.sql.Date;

@Builder
@Value
public class BonusResponse {

    int id;
    private String bonusName;
    private String bonusType;
    private Date startTime;
    private Date endTime;
}
