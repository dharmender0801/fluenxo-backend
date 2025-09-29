package com.asc.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asc.auth.model.CampaignClickInfo;

public interface CampaignClickInfoRepository extends JpaRepository<CampaignClickInfo, Long> {

	@Query(value = "SELECT 'campaign_total' AS metric_type, campaign_id AS campaign_id, NULL AS influencer_id, NULL AS city, NULL AS device, SUM(clicks) AS total_clicks, SUM(reach) AS total_reach "
			+ "FROM campaign_audience_summary " + "GROUP BY campaign_id " + "UNION ALL "
			+ "SELECT 'influencer_total', NULL, influencer_id, NULL, NULL, SUM(clicks), SUM(reach) "
			+ "FROM campaign_audience_summary " + "GROUP BY influencer_id " + "UNION ALL "
			+ "SELECT 'device_total', campaign_id, NULL, NULL, device, SUM(clicks), NULL "
			+ "FROM campaign_audience_summary " + "GROUP BY campaign_id, device " + "UNION ALL "
			+ "SELECT 'city_total', NULL, influencer_id, city, NULL, SUM(clicks), NULL "
			+ "FROM campaign_audience_summary " + "GROUP BY influencer_id, city", nativeQuery = true)
	List<Object[]> getAllMetrics();

	long countByIpAddressAndDeviceId(String ipAddress, String deviceId);
}
