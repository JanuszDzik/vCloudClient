package com.example.vcloudclient.spring.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vm {
    @Id
    private String id;
    private String name;
    private String vappName;
    private String guestOs;
    private int numberOfCpus;
    private String status;
    private String providerId;
}
