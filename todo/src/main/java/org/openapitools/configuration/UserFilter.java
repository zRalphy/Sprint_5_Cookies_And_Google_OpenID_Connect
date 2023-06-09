package org.openapitools.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class UserFilter extends AbstractAuthenticationProcessingFilter {

	protected UserFilter() {
		super(new AntPathRequestMatcher("/user/login", "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		String username;
		String password;

		try {
			Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
			username = requestMap.get("userName");
			password = requestMap.get("password");
		} catch (IOException e) {
			throw new AuthenticationServiceException("Can not parse user data from request input stream", e);
		}

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
}
