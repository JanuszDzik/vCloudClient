package com.example.vcloudclient.spring.core.repository;

import com.example.vcloudclient.spring.core.model.EdgeGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeGatewayRepository extends JpaRepository<EdgeGateway, String> {
}
