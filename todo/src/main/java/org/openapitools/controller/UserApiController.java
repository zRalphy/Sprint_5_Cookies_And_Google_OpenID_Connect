package org.openapitools.controller;

import java.security.Principal;

import org.openapitools.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
@RequestMapping("${openapi.todo.base-path:}")
public class UserApiController implements UserApi {
	public static final String OAUTH_USER = "OAUTH_USER";

	private final NativeWebRequest request;

	@Autowired
	public UserApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	@Secured({OAUTH_USER})
	public ResponseEntity<String> getUserInfo() throws ApiException {
		Principal token = request.getUserPrincipal();
		String userName;
		if (token instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken defaultToken = (OAuth2AuthenticationToken) request.getUserPrincipal();
			if (defaultToken == null) {
				throw new ApiException(HttpStatus.FORBIDDEN.value());
			}
			userName = defaultToken.getPrincipal().getAttributes().get("name").toString();
			return new ResponseEntity<>(userName, HttpStatus.OK);

		} else {
			throw new ApiException(HttpStatus.FORBIDDEN.value());
		}
	}
}
