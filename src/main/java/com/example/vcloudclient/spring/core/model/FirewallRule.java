package com.example.vcloudclient.spring.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FirewallRule {
    @Id
    private int id;
    private String edgeGatewayId;
    private String name;
    private String description;
    private String ruleType;
    private boolean enabled;
    private String action;
    private String xmlBody;
}
