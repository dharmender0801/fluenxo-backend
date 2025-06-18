package com.asc.auth.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.asc.auth.security.TokenFilter;
import com.asc.auth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.asc.auth.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.asc.auth.security.oauth2.OAuth2LoginSuccessHandler;
import com.asc.auth.service.CustomOAuth2UserService;
import com.asc.auth.service.CustomOidcUserService;
import com.asc.auth.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private UserService userService;
	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	@Autowired
	CustomOAuth2UserService customOAuth2UserService;
	@Autowired
	CustomOidcUserService customOidcUserService;
	@Autowired
	OAuth2AuthenticationFailureHandler auth2AuthenticationFailureHandler;

	private List<String> allowedOrigins = Arrays.asList("capacitor://localhost", "ionic://localhost",
			"https://localhost", "http://localhost:9090", "http://localhost:3000", "http://localhost:4200",
			"https://wms-dev.goalfa.in", "https://wms.goalfa.in", "https://alfa.goalfa.in",
			"https://wms-dev.apollosupplychain.com", "https://wms.apollosupplychain.com",
			"https://havells.apollosupplychain.com", "https://beta.apollosupplychain.com",
			"https://ptl.apollosupplychain.com", "https://dev.apollosupplychain.com",
			"https://uat.apollosupplychain.com", "https://ems.apollosupplychain.com",
			"https://ems-dev.apollosupplychain.com", "https://pms.apollosupplychain.com",
			"https://lms-dev.apollosupplychain.com", "https://dev.drinkxtcy.com", "https://mission.drinkxtcy.com");

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						request -> request
								.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**",
										"/swagger-ui/**", "/webjars/**", "/csrf**/**", "/templates/**", "/index.html**",
										"/configuration/ui", "/configuration/security", "/auth/**", "/login/**",
										"/oauth2/**", "/oauth2/authorization/**")
								.permitAll().anyRequest().authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.oauth2Login(oauth2 -> oauth2
						.authorizationEndpoint(authorization -> authorization
								.authorizationRequestRepository(cookieAuthorizationRequestRepository()))
						.redirectionEndpoint(redir -> redir.baseUri("/login/oauth2/code/*"))
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)
								.oidcUserService(customOidcUserService))
						.successHandler(oAuth2LoginSuccessHandler).failureUrl("/auth/oauth-failure"))
				.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
		return http.build();
	}

	@Bean
	TokenFilter tokenAuthenticationFilter() {
		return new TokenFilter();
	}

	@Bean
	HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(allowedOrigins);
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
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
