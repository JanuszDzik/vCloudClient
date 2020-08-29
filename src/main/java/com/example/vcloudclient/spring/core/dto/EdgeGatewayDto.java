package com.example.vcloudclient.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EdgeGatewayDto {
    private String id;
    private String name;
    private String orgName;
    private String orgVdcName;
    private String providerId;
}
