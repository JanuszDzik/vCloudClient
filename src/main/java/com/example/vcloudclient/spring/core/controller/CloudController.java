package com.example.vcloudclient.spring.core.controller;

import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.dto.EdgeGatewayDto;
import com.example.vcloudclient.spring.core.dto.VcloudProviderWrapperDto;
import com.example.vcloudclient.spring.core.dto.VmDto;
import com.example.vcloudclient.spring.core.service.CloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

@RestController
@RequestMapping("/")
public class CloudController {
    private final CloudService cloudService;

    @Autowired
    public CloudController(CloudService cloudService){
        this.cloudService = cloudService;
    }

    //API endpoints

    @GetMapping("/cloud/api/vm/list")
    public ResponseEntity<List<VmDto>> listAllVms(@RequestParam(value = "providerId", required = false) String providerId) {
        final List<VmDto> allVms = cloudService.getAllVms(providerId);
        return ResponseEntity.ok().body(allVms);
    }

    @GetMapping("/cloud/api/edgeGateways/list")
    public ResponseEntity<List<EdgeGatewayDto>> listAllEgdeGateways(@RequestParam(value = "providerId", required = false) String providerId) {
        final List<EdgeGatewayDto> allEdgeGateways = cloudService.getAllEdgeGateways(providerId);
        return ResponseEntity.ok().body(allEdgeGateways);
    }

    @GetMapping("/cloud/api/vms")
    public ResponseEntity<Object> getProviderVms(@RequestParam(value = "providerId") String providerId, @RequestParam(value = "providerPassword") String providerPassword) throws ParserConfigurationException {
        cloudService.getVmsFromVcloud(providerId, cloudService.getVcloudApiToken(providerId, providerPassword));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cloud/api/edgeGateways")
    public ResponseEntity<Object> getProviderEdgeGateways(@RequestParam(value = "providerId") String providerId, @RequestParam(value = "providerPassword") String providerPassword) throws ParserConfigurationException {
        cloudService.getEdgesFromVcloud(providerId, cloudService.getVcloudApiToken(providerId, providerPassword));
        return ResponseEntity.ok().build();
    }



    //UI Endpoints

    @GetMapping("/home")
    public ModelAndView init(ModelAndView modelAndView){
        final String homeTitle = "vCloud Client";
        modelAndView.setViewName("home");
        modelAndView.addObject("homeTitle", homeTitle);
        return modelAndView;
    }

    @GetMapping("/AddProvider")
    public ModelAndView addProvider(ModelAndView modelAndView){
        modelAndView.setViewName("add_vcloud_provider");
        modelAndView.addObject("cloudProvider", new CloudProviderDto());
        return modelAndView;
    }

    @GetMapping("/ListVms")
    public ModelAndView listVms(ModelAndView modelAndView) {
        modelAndView.setViewName("list_vms");
        modelAndView.addObject("vms", cloudService.listAllVms());
        return modelAndView;
    }

    @GetMapping("/ListEdgeGateways")
    public ModelAndView listEdgeGateways(ModelAndView modelAndView) {
        modelAndView.setViewName("list_edge_gateways");
        modelAndView.addObject("edgegateways", cloudService.listAllEdgeGateways());
        return modelAndView;
    }

    @GetMapping("/ListProviders")
    public ModelAndView listProviders(ModelAndView modelAndView) {
        modelAndView.setViewName("list_providers");
        modelAndView.addObject("providers", cloudService.listAllCloudProviders());
        return modelAndView;
    }

    @GetMapping("/")
    public ModelAndView GetCloudDataForm(ModelAndView modelAndView) {
        if (cloudService.noProviderPresent()) {
            modelAndView.setViewName("add_vcloud_provider");
            modelAndView.addObject("cloudProvider", new CloudProviderDto());
        } else {
            modelAndView.setViewName("get_cloud_data");
            modelAndView.addObject("form", new VcloudProviderWrapperDto(cloudService.listAllCloudProviders()));
        }
        return modelAndView;
    }

    @GetMapping("/GetCloudData")
    public ModelAndView GetCloudData(@ModelAttribute VcloudProviderWrapperDto form, ModelAndView modelAndView) throws ParserConfigurationException {
        List<CloudProviderDto> cloudProviders = cloudService.listAllCloudProviders();
        for (int j=0; j<form.getProviders().size(); j++){
            cloudProviders.get(j).setPassword(form.getProviders().get(j).getPassword());
        }
        cloudService.setCloudProviders(cloudProviders);
        cloudService.getAllCloudData();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping("/vm-console/{id}")
    public ModelAndView launchConsole(@PathVariable("id") String id, ModelAndView modelAndView) throws ParserConfigurationException {
        final String homeTitle = "Console";
        System.out.println("ID=" + id);
        String consoleUrl = cloudService.getVmConsoleUrl(id);
        System.out.println("Console URL:" + consoleUrl);
        modelAndView.setViewName("vm_console");
        modelAndView.addObject("homeTitle", homeTitle);
        modelAndView.addObject("consoleUrl",consoleUrl);
        return modelAndView;
    }

    @GetMapping("/vm-poweroff/{id}")
    public ModelAndView powerOff(@PathVariable("id") String id, ModelAndView modelAndView) throws ParserConfigurationException {
        cloudService.doVmPowerAction(id, PowerAction.powerOff.toString());
        modelAndView.setViewName("list_vms");
        modelAndView.addObject("vms", cloudService.listAllVms());
        return modelAndView;
    }

    @GetMapping("/vm-poweron/{id}")
    public ModelAndView powerOn(@PathVariable("id") String id, ModelAndView modelAndView) throws ParserConfigurationException {
        cloudService.doVmPowerAction(id, PowerAction.powerOn.toString());
        modelAndView.setViewName("list_vms");
        modelAndView.addObject("vms", cloudService.listAllVms());
        return modelAndView;
    }

    @GetMapping("/vm-shutdown/{id}")
    public ModelAndView shutdown(@PathVariable("id") String id, ModelAndView modelAndView) throws ParserConfigurationException {
        cloudService.doVmPowerAction(id, PowerAction.shutdown.toString());
        modelAndView.setViewName("list_vms");
        modelAndView.addObject("vms", cloudService.listAllVms());
        return modelAndView;
    }

}
