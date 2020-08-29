package com.example.vcloudclient.spring.core.converter;

import com.example.vcloudclient.spring.core.dto.VmDto;
import com.example.vcloudclient.spring.core.model.Vm;

public class VmConverter {
    public static VmDto mapToDto(Vm vm) {
        return VmDto.builder()
                .id(vm.getId())
                .name(vm.getName())
                .vappName(vm.getVappName())
                .guestOs(vm.getGuestOs())
                .numberOfCpus(vm.getNumberOfCpus())
                .status(vm.getStatus())
                .providerId(vm.getProviderId())
                .build();
    }

    public static Vm mapFromDto(VmDto vm) {
        return Vm.builder()
                .id(vm.getId())
                .name(vm.getName())
                .vappName(vm.getVappName())
                .guestOs(vm.getGuestOs())
                .numberOfCpus(vm.getNumberOfCpus())
                .status(vm.getStatus())
                .providerId(vm.getProviderId())
                .build();
    }
}
