package com.example.vcloudclient.spring.core.controller;

import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UiController {
    private final UiService uiService;

    @Autowired
    public UiController(UiService uiService){
        this.uiService=uiService;
    }

    @GetMapping("/")
    public String init(Model model){
        final String homeTitle = "vCloud Client";
        model.addAttribute("homeTitle", homeTitle);
        return "home";
    }

    @GetMapping("/AddProvider")
    public String addProvider(Model model){
        final String homeTitle = "Add vCloud Provider";
        model.addAttribute("homeTitle", homeTitle);
        model.addAttribute("cloudProvider", new CloudProviderDto());
        return "addvcloudprovider";
    }

    @GetMapping("/ListVms")
    public ModelAndView listVms(ModelAndView modelAndView) {
        modelAndView.setViewName("listvms");
        modelAndView.addObject("vms", uiService.listAllVms());
        return modelAndView;
    }

    @GetMapping("/ListProviders")
    public ModelAndView listProviders(ModelAndView modelAndView) {
        modelAndView.setViewName("listproviders");
        modelAndView.addObject("providers", uiService.listAllCloudProviders());
        return modelAndView;
    }
}
