package com.example.vcloudclient.spring.core.repository;

import com.example.vcloudclient.spring.core.model.CloudProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudProviderRepository  extends JpaRepository<CloudProvider, String> {
}
