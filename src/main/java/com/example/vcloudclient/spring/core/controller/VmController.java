package com.example.vcloudclient.spring.core.controller;

import com.example.vcloudclient.spring.core.dto.VmDto;
import com.example.vcloudclient.spring.core.service.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

@RestController
@RequestMapping("/api/vm")
public class VmController {
    private final VmService vmService;

    @Autowired
    public VmController(VmService vmService){
        this.vmService=vmService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<VmDto>> listAllVms(@RequestParam(value = "providerId", required = false) String providerId) {
        final List<VmDto> allVms = vmService.getAllVms(providerId);
        return ResponseEntity.ok().body(allVms);
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getProviderVms(@RequestParam(value = "providerId") String providerId, @RequestParam(value = "providerPassword") String providerPassword) throws ParserConfigurationException {
        vmService.getVmsFromVcloud(providerId, vmService.getVcloudApiToken(providerId, providerPassword));
        return ResponseEntity.ok().build();
    }
}
