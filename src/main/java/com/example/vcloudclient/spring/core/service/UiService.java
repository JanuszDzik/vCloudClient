package com.example.vcloudclient.spring.core.service;

import com.example.vcloudclient.spring.core.converter.CloudProviderConverter;
import com.example.vcloudclient.spring.core.converter.VmConverter;
import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.dto.VmDto;
import com.example.vcloudclient.spring.core.repository.CloudProviderRepository;
import com.example.vcloudclient.spring.core.repository.VmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UiService {
    private VmRepository vmRepository;
    private CloudProviderRepository cloudProviderRepository;

    @Autowired
    public UiService(VmRepository vmRepository, CloudProviderRepository cloudProviderRepository){
        this.vmRepository = vmRepository;
        this.cloudProviderRepository = cloudProviderRepository;
    }

    public List<VmDto> listAllVms() {
        return vmRepository.findAll().stream().map(VmConverter::mapToDto).collect(Collectors.toList());
    }

    public List<CloudProviderDto> listAllCloudProviders() {
        return cloudProviderRepository.findAll().stream().map(CloudProviderConverter::mapToDto).collect(Collectors.toList());
    }
}
