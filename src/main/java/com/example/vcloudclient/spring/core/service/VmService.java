package com.example.vcloudclient.spring.core.service;

import com.example.vcloudclient.spring.core.converter.VmConverter;
import com.example.vcloudclient.spring.core.dto.VmDto;
import com.example.vcloudclient.spring.core.model.Vm;
import com.example.vcloudclient.spring.core.repository.CloudProviderRepository;

import com.example.vcloudclient.spring.core.repository.VmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.remote.client.HttpHeaderInterceptor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class VmService {
    private CloudProviderRepository cloudProviderRepository;
    private VmRepository vmRepository;
    private RestTemplate restTemplate;

    @Autowired
    public VmService(CloudProviderRepository cloudProviderRepository, VmRepository vmRepository, RestTemplate restTemplate){
        this.cloudProviderRepository = cloudProviderRepository;
        this.vmRepository = vmRepository;
        this.restTemplate = restTemplate;
    }

    public List<VmDto> getAllVms(String providerId){
        Predicate<Vm> filter = vm -> {
            if (providerId != null) {
                return vm.getProviderId() == providerId;
            }
            return true;
        };
        return vmRepository.findAll().stream().filter(filter).map(VmConverter::mapToDto).collect(Collectors.toList());
    }

    public String getVcloudApiToken(String providerId, String providerPassword) {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        String notEncoded = cloudProviderRepository.findById(providerId).get().getUser() + "@" +
                cloudProviderRepository.findById(providerId).get().getId() + ":" + providerPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
        interceptors.add(new HttpHeaderInterceptor("Accept", "application/*+xml;version=33.0"));
        interceptors.add(new HttpHeaderInterceptor("Authorization", "Basic " + encodedAuth));
        restTemplate.setInterceptors(interceptors);
        final ResponseEntity<String> forEntity =
                restTemplate.postForEntity(cloudProviderRepository.findById(providerId).get().getHost() + "/api/sessions",String.class,String.class);
        return forEntity.getHeaders().getFirst("X-VMWARE-VCLOUD-ACCESS-TOKEN");
    }

    public void getVmsFromVcloud(String providerId, String vcloudToken) throws ParserConfigurationException {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HttpHeaderInterceptor("Accept", "application/*+xml;version=33.0"));
        interceptors.add(new HttpHeaderInterceptor("Authorization", "Bearer " + vcloudToken));
        restTemplate.setInterceptors(interceptors);
        final ResponseEntity<String> forEntity = restTemplate.getForEntity(cloudProviderRepository.findById(providerId).get().getHost() +
                "/api/query?type=vm&isVAppTemplate=false&format=idrecords", String.class);
        //System.out.println("External service returned: " + forEntity.getBody());
        Document doc = convertStringToXMLDocument(forEntity.getBody());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("VMRecord");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)node;
                Vm vm = new Vm(
                        elem.getAttribute("id"),
                        elem.getAttribute("name"),
                        elem.getAttribute("containerName"),
                        elem.getAttribute("guestOs"),
                        Integer.parseInt(elem.getAttribute("numberOfCpus")),
                        elem.getAttribute("status"),
                        providerId
                );
                //System.out.println(vm);
                if (elem.getAttribute("isVAppTemplate").equals("false")){
                    vmRepository.save(vm);
                };
            }
        }
    }

    public void getEdgesFromVcloud(String providerId, String vcloudToken) throws ParserConfigurationException {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HttpHeaderInterceptor("Accept", "application/*+xml;version=33.0"));
        interceptors.add(new HttpHeaderInterceptor("Authorization", "Bearer " + vcloudToken));
        restTemplate.setInterceptors(interceptors);
        final ResponseEntity<String> forEntity = restTemplate.getForEntity(cloudProviderRepository.findById(providerId).get().getHost() +
                "/api/query?type=vm&isVAppTemplate=false&format=idrecords", String.class);
        //System.out.println("External service returned: " + forEntity.getBody());
        Document doc = convertStringToXMLDocument(forEntity.getBody());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("VMRecord");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)node;
                Vm vm = new Vm(
                        elem.getAttribute("id"),
                        elem.getAttribute("name"),
                        elem.getAttribute("containerName"),
                        elem.getAttribute("guestOs"),
                        Integer.parseInt(elem.getAttribute("numberOfCpus")),
                        elem.getAttribute("status"),
                        providerId
                );
                //System.out.println(vm);
                if (elem.getAttribute("isVAppTemplate").equals("false")){
                    vmRepository.save(vm);
                };
            }
        }
    }

    private static Document convertStringToXMLDocument(String xmlString){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
