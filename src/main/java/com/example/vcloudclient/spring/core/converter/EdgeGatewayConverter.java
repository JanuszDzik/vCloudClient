package com.example.vcloudclient.spring.core.converter;

import com.example.vcloudclient.spring.core.dto.EdgeGatewayDto;
import com.example.vcloudclient.spring.core.model.EdgeGateway;

public class EdgeGatewayConverter {
    public static EdgeGatewayDto mapToDto(EdgeGateway edgeGateway) {
        return EdgeGatewayDto.builder()
                .id(edgeGateway.getId())
                .name(edgeGateway.getName())
                .orgName(edgeGateway.getOrgName())
                .orgVdcName(edgeGateway.getOrgVdcName())
                .providerId(edgeGateway.getProviderId())
                .build();
    }

    public static EdgeGateway mapFromDto(EdgeGatewayDto edgeGateway) {
        return EdgeGateway.builder()
                .id(edgeGateway.getId())
                .name(edgeGateway.getName())
                .orgName(edgeGateway.getOrgName())
                .orgVdcName(edgeGateway.getOrgVdcName())
                .providerId(edgeGateway.getProviderId())
                .build();
    }
}
