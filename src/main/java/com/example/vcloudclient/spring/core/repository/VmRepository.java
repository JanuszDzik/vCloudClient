package com.example.vcloudclient.spring.core.repository;

import com.example.vcloudclient.spring.core.model.Vm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VmRepository extends JpaRepository<Vm, String> {
}
