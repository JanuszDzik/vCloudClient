package com.example.vcloudclient.spring.core.service;

import com.example.vcloudclient.spring.core.converter.CloudProviderConverter;
import com.example.vcloudclient.spring.core.converter.EdgeGatewayConverter;
import com.example.vcloudclient.spring.core.converter.VmConverter;
import com.example.vcloudclient.spring.core.dto.CloudProviderDto;
import com.example.vcloudclient.spring.core.dto.EdgeGatewayDto;
import com.example.vcloudclient.spring.core.dto.VmDto;
import com.example.vcloudclient.spring.core.model.EdgeGateway;
import com.example.vcloudclient.spring.core.model.Vm;
import com.example.vcloudclient.spring.core.repository.CloudProviderRepository;

import com.example.vcloudclient.spring.core.repository.EdgeGatewayRepository;
import com.example.vcloudclient.spring.core.repository.VmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.remote.client.HttpHeaderInterceptor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
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

import static com.example.vcloudclient.spring.core.service.Conversions.convertStringToXMLDocument;

@Service
public class CloudService {
    private CloudProviderRepository cloudProviderRepository;
    private VmRepository vmRepository;
    private EdgeGatewayRepository edgeGatewayRepository;
    private RestTemplate restTemplate;
    private List<CloudProviderDto> cloudProviders;

    public void setCloudProviders(List<CloudProviderDto> cloudProviders) {
        this.cloudProviders = cloudProviders;
    }

    @Autowired
    public CloudService(CloudProviderRepository cloudProviderRepository, VmRepository vmRepository, EdgeGatewayRepository edgeGatewayRepository, RestTemplate restTemplate){
        this.cloudProviderRepository = cloudProviderRepository;
        this.vmRepository = vmRepository;
        this.edgeGatewayRepository = edgeGatewayRepository;
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

    public List<EdgeGatewayDto> getAllEdgeGateways(String providerId){
        Predicate<EdgeGateway> filter = edgeGateway -> {
            if (providerId != null) {
                return edgeGateway.getProviderId() == providerId;
            }
            return true;
        };
        return edgeGatewayRepository.findAll().stream().filter(filter).map(EdgeGatewayConverter::mapToDto).collect(Collectors.toList());
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
                "/api/query?type=vm&isVAppTemplate=false", String.class);
        //System.out.println("External service returned: " + forEntity.getBody());
        Document doc = convertStringToXMLDocument(forEntity.getBody());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("VMRecord");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)node;
                Vm vm = new Vm(
                        elem.getAttribute("href").substring(elem.getAttribute("href").indexOf("vm-")),
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
                "/api/query?type=edgeGateway&format=idrecords", String.class);
        System.out.println("RETURN:");
        System.out.println("External service returned: " + forEntity.getBody());
        Document doc = convertStringToXMLDocument(forEntity.getBody());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("EdgeGatewayRecord");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)node;
                EdgeGateway edgeGateway = new EdgeGateway(
                        elem.getAttribute("id"),
                        elem.getAttribute("name"),
                        elem.getAttribute("orgName"),
                        elem.getAttribute("orgVdcName"),
                        providerId
                );
                System.out.println("OBJECT:");
                System.out.println(edgeGateway);
                edgeGatewayRepository.save(edgeGateway);
            }
        }
    }

    public String getVmConsoleTicket(String id, String vcloudToken) throws ParserConfigurationException {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HttpHeaderInterceptor("Accept", "application/*+xml;version=33.0"));
        interceptors.add(new HttpHeaderInterceptor("Authorization", "Bearer " + vcloudToken));
        restTemplate.setInterceptors(interceptors);
        System.out.println("ticket url:" + cloudProviderRepository.findById(vmRepository.findById(id).get().getProviderId()).get().getHost() + "/api/vApp/" + id + "/screen/action/acquireMksTicket");
        final ResponseEntity<String> forEntity = restTemplate.postForEntity( cloudProviderRepository.findById(vmRepository.findById(id).get().getProviderId()).get().getHost() + "/api/vApp/" + id + "/screen/action/acquireMksTicket", String.class, String.class);
        Document doc = convertStringToXMLDocument(forEntity.getBody());
        doc.getDocumentElement().normalize();
       return doc.getElementsByTagName("Ticket").item(0).getTextContent();

    }

    @Scheduled(fixedRate = 1500000)
    public void getAllCloudData() throws ParserConfigurationException {
        if (cloudProviders != null) {
            for (CloudProviderDto provider: cloudProviders) {
                provider.setToken(getVcloudApiToken(provider.getId(), provider.getPassword()));
                getVmsFromVcloud(provider.getId(), provider.getToken());
                getEdgesFromVcloud(provider.getId(), provider.getToken());
            }
        }
    }



    public List<VmDto> listAllVms() {
        return vmRepository.findAll().stream().map(VmConverter::mapToDto).collect(Collectors.toList());
    }

    public List<EdgeGatewayDto> listAllEdgeGateways() {
        return edgeGatewayRepository.findAll().stream().map(EdgeGatewayConverter::mapToDto).collect(Collectors.toList());
    }

    public List<CloudProviderDto> listAllCloudProviders() {
        return cloudProviderRepository.findAll().stream().map(CloudProviderConverter::mapToDto).collect(Collectors.toList());
    }

    public String getVmConsoleUrl(String id) throws ParserConfigurationException {
        String providerId = vmRepository.findById(id).get().getProviderId();
        return cloudProviderRepository.findById(providerId).get().getHost().replace("https://vcloud.exea.pl","wss://rc.exea.pl:443") + "/902;" + getVmConsoleTicket(id, getToken(providerId));
    }

    public void doVmPowerAction(String id, String powerAction) throws ParserConfigurationException {
        String providerId = vmRepository.findById(id).get().getProviderId();
        String vmPowerActionUrl = cloudProviderRepository.findById(providerId).get().getHost() + "/api/vApp/" + id + "/power/action/" + powerAction;
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        System.out.println(vmPowerActionUrl);
        System.out.println(providerId);
        interceptors.add(new HttpHeaderInterceptor("Accept", "application/*+xml;version=33.0"));
        interceptors.add(new HttpHeaderInterceptor("Authorization", "Bearer " + getToken(providerId)));
        restTemplate.setInterceptors(interceptors);
        final ResponseEntity<String> forEntity = restTemplate.postForEntity(vmPowerActionUrl, String.class, String.class);
    }

    private String getToken(String id){
        for (CloudProviderDto cloudProvider:cloudProviders) {
            if (cloudProvider.getId().equals(id))
                return cloudProvider.getToken();
        }
        return null;
    }

    public boolean noProviderPresent() {
        return cloudProviderRepository.count() == 0;
    }
}
