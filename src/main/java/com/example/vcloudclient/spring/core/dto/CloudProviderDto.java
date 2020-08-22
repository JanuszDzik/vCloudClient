package com.example.vcloudclient.spring.core.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudProviderDto {
    @Id
    private String id;
    private String host;
    private String user;
}
