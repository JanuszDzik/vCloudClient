package com.example.vcloudclient.spring.core.service;

import com.example.vcloudclient.spring.core.converter.CloudProviderConverter;
import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.model.CloudProvider;
import com.example.vcloudclient.spring.core.repository.CloudProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudProviderService {
    private CloudProviderRepository cloudProviderRepository;

    @Autowired
    public CloudProviderService(CloudProviderRepository cloudProviderRepository){
        this.cloudProviderRepository = cloudProviderRepository; }

    public List<CloudProviderDto> getAllCloudProviders(){
        return cloudProviderRepository.findAll().stream().map(CloudProviderConverter::mapToDto).collect(Collectors.toList());
    }

    public void addNewCloudProvider(CloudProviderDto newCloudProvider) {
        final CloudProvider cloudProvider = CloudProviderConverter.mapFromDto(newCloudProvider);
        cloudProviderRepository.save(cloudProvider);
    }

    public void deleteCloudProvider(String id) {
        cloudProviderRepository.deleteById(id);
    }



}
