package com.example.vcloudclient.spring.core.converter;

import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.model.CloudProvider;

public class CloudProviderConverter {
    public static CloudProviderDto mapToDto(CloudProvider cloudProvider) {
        return CloudProviderDto.builder()
                .id(cloudProvider.getId())
                .host(cloudProvider.getHost())
                .user(cloudProvider.getUser())
                .build();
    }

    public static CloudProvider mapFromDto(CloudProviderDto cloudProvider) {
        return CloudProvider.builder()
                .id(cloudProvider.getId())
                .host(cloudProvider.getHost())
                .user(cloudProvider.getUser())
                .build();
    }
}
