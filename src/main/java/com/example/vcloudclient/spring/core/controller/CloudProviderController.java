package com.example.vcloudclient.spring.core.controller;

import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.service.CloudProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/cloud-provider")
public class CloudProviderController {
    private final CloudProviderService cloudProviderService;

    @Autowired
    public CloudProviderController(CloudProviderService cloudProviderService){
        this.cloudProviderService = cloudProviderService;
    }

    //API endpoints

    @GetMapping("/api/list")
    public ResponseEntity<List<CloudProviderDto>> getAllCloudProviders() {
        final List<CloudProviderDto> allCloudProviders = cloudProviderService.getAllCloudProviders();
        return ResponseEntity.ok().body(allCloudProviders);
    }

    @PostMapping("/api/create")
    public ResponseEntity<Object> createCloudProvider(@ModelAttribute CloudProviderDto cloudProvider){
        cloudProviderService.addNewCloudProvider(cloudProvider);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //UI endpoints

    @PostMapping("/create")
    public ModelAndView createCloudProviderUi(@ModelAttribute CloudProviderDto cloudProvider, ModelAndView modelAndView){
        cloudProviderService.addNewCloudProvider(cloudProvider);
        modelAndView.setViewName("ok");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteProvider(@PathVariable("id") String id,  ModelAndView modelAndView) {
        cloudProviderService.deleteCloudProvider(id);
        modelAndView.setViewName("ok");
        return modelAndView;
    }

}
