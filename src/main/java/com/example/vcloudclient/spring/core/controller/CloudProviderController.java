package com.example.vcloudclient.spring.core.controller;

import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.service.CloudProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cloud-provider")
public class CloudProviderController {
    private final CloudProviderService cloudProviderService;

    @Autowired
    public CloudProviderController(CloudProviderService cloudProviderService){
        this.cloudProviderService = cloudProviderService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<CloudProviderDto>> getAllCloudProviders() {
        final List<CloudProviderDto> allCloudProviders = cloudProviderService.getAllCloudProviders();
        return ResponseEntity.ok().body(allCloudProviders);
    }

    @PostMapping
    public ResponseEntity<Object> createCloudProvider(@RequestBody CloudProviderDto cloudProvider){
        cloudProviderService.addNewCloudProvider(cloudProvider);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
