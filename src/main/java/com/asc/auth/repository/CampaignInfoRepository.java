package com.asc.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.asc.auth.model.CampaignInfo;

public interface CampaignInfoRepository extends JpaRepository<CampaignInfo, Long> {

	Page<CampaignInfo> findAll(Specification<?> criteria, Pageable pageable);

}
