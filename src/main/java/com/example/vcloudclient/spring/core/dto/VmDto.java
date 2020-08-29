package com.example.vcloudclient.spring.core.dto;

import lombok.*;

import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VmDto {
    @Id
    private String id;
    private String name;
    private String vappName;
    private String guestOs;
    private int numberOfCpus;
    private String status;
    private String providerId;
}
