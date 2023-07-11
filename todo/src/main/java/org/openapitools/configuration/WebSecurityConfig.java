package org.openapitools.configuration;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.openapitools.controller.UserApiController.OAUTH_USER;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http
				.cors().disable()
				.csrf().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.and()
				.authorizeHttpRequests()
				.requestMatchers("/user/**").permitAll()
				.requestMatchers("/oauth2/**").permitAll()
				.requestMatchers("/user/info").hasRole(OAUTH_USER)
				.anyRequest().authenticated()
				.and()
				.oauth2Login()
				.and()
				.build();
	}
}
