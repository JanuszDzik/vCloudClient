package com.example.vcloudclient.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class VcloudProviderWrapperDto {
    private List<CloudProviderDto> providers;
}
