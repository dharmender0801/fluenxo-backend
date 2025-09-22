package com.asc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.CampaignClickInfo;

public interface CampaignClickInfoRepository extends JpaRepository<CampaignClickInfo, Long> {

}
