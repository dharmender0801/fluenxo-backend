package com.asc.auth.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.Ordered;
import org.springframework.data.domain.AuditorAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Priority;

@Configuration
@EnableSpringConfigured
@EnableAsync
@Priority(value = 1)
public class WebConfiguration implements WebMvcConfigurer {
	private List<String> allowedOrigins = Arrays.asList("capacitor://localhost", "ionic://localhost",
			"https://localhost", "http://localhost:9090", "http://localhost:3000", "http://localhost:4200",
			"https://wms-dev.goalfa.in", "https://wms.goalfa.in", "https://alfa.goalfa.in",
			"https://wms-dev.apollosupplychain.com", "https://wms.apollosupplychain.com",
			"https://havells.apollosupplychain.com", "https://beta.apollosupplychain.com",
			"https://ptl.apollosupplychain.com", "https://dev.apollosupplychain.com",
			"https://uat.apollosupplychain.com", "https://ems.apollosupplychain.com",
			"https://ems-dev.apollosupplychain.com", "https://pms.apollosupplychain.com",
			"https://pms-dev.apollosupplychain.com", "https://dev.drinkxtcy.com", "https://mission.drinkxtcy.com");

	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new AuditorAwareImpl();
	}

	@Bean
	Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(200);
		executor.setThreadNamePrefix("AsyncExecutor-");
		executor.initialize();
		return executor;
	}

	@Bean(name = "defaultTaskScheduler")
	public TaskScheduler defaultTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		scheduler.setThreadNamePrefix("default-scheduler-");
		scheduler.initialize();
		return scheduler;
	}

	@Bean(name = "singleThreadTaskScheduler")
	public TaskScheduler singleThreadTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("single-thread-scheduler-");
		scheduler.initialize();
		return scheduler;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(5);
		scheduler.setThreadNamePrefix("ASC-Scheduler-");
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		return scheduler;
	}

	@Bean
	FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		allowedOrigins.forEach(config::addAllowedOrigin);
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addExposedHeader("Authorization");
		config.addExposedHeader("Content-Type");
		config.addExposedHeader("X-AUTH-TOKEN");
		config.addExposedHeader("AUTH-TOKEN");
		config.addExposedHeader("Device-Type");
		config.addExposedHeader("VER");
		config.addExposedHeader("AppVersionNo");
		config.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins(allowedOrigins.toArray(new String[0]))
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH").allowedHeaders("*")
				.exposedHeaders("Authorization", "X-AUTH-TOKEN");
	}

}