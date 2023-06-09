/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.5.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openapitools.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.openapitools.api.ApiException;
import org.openapitools.model.dto.UserRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
@Tag(name = "users", description = "the users API")
public interface UserApi {

	/**
	 * POST /user/register : Creates new user
	 *
	 * @param userRegisterRequest
	 * 		(required)
	 * @return User created (status code 200)
	 * or Request body is invalid (status code 400)
	 * or User already exists (status code 409)
	 */
	@Operation(
			operationId = "register",
			summary = "Creates new user",
			tags = {"users"},
			responses = {
					@ApiResponse(responseCode = "200", description = "User created"),
					@ApiResponse(responseCode = "400", description = "Request body is invalid"),
					@ApiResponse(responseCode = "409", description = "User already exists")
			}
	)
	@RequestMapping(
			method = RequestMethod.POST,
			value = "/user/register",
			consumes = {"application/json"}
	)
	default ResponseEntity<Void> register(
			@Parameter(name = "UserRegisterRequest", description = "", required = true) @Valid @RequestBody UserRegisterRequest userRegisterRequest
	) throws ApiException {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	/**
	 * GET /user/info : Get information about user
	 *
	 * @return Information about user (status code 200)
	 * or resources is forbidden (status code 403)
	 */
	@Operation(
			operationId = "info",
			summary = "Get information about user",
			tags = {"users"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Accepted"),
					@ApiResponse(responseCode = "403", description = "Resources is forbidden"),
			}
	)
	@RequestMapping(
			method = RequestMethod.GET,
			value = "/user/info"
	)
	default ResponseEntity<String> getUserInfo() throws ApiException {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
