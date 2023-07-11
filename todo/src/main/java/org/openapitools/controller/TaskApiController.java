package org.openapitools.controller;

import jakarta.annotation.security.RolesAllowed;

import java.util.List;

import org.openapitools.api.ApiException;
import org.openapitools.model.dto.CreateReminderRequest;
import org.openapitools.model.dto.TaskCreateRequest;
import org.openapitools.model.dto.TaskResponse;
import org.openapitools.model.dto.TaskUpdateRequest;
import org.openapitools.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.openapitools.controller.UserApiController.OAUTH_USER;

@Controller
@RequestMapping("${openapi.todo.base-path:}")
public class TaskApiController implements TaskApi {
	private final TaskService taskService;

	@Autowired
	public TaskApiController(TaskService taskService) {
		this.taskService = taskService;
	}

	@Override
	public ResponseEntity<TaskResponse> getTask(Long taskId, @AuthenticationPrincipal OidcUser oidcUser) throws ApiException {
		return new ResponseEntity<>(taskService.getTaskByUserIdAndTaskId(taskId, oidcUser), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<TaskResponse>> listTasks(@AuthenticationPrincipal OidcUser oidcUser) {
		return new ResponseEntity<>(taskService.getAllTasksByUserId(oidcUser), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TaskResponse> createTask(TaskCreateRequest taskCreateRequest, @AuthenticationPrincipal OidcUser oidcUser) throws ApiException {
		return new ResponseEntity<>(taskService.createTask(taskCreateRequest, oidcUser), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TaskResponse> updateTask(Long taskId, TaskUpdateRequest taskUpdateRequest, @AuthenticationPrincipal OidcUser oidcUser)
			throws ApiException {
		return new ResponseEntity<>(taskService.updateTask(taskId, taskUpdateRequest, oidcUser), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteTask(Long taskId, @AuthenticationPrincipal OidcUser oidcUser) throws ApiException {
		taskService.deleteTask(taskId, oidcUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	//@RolesAllowed(OAUTH_USER)
	public ResponseEntity<TaskResponse> createTaskReminder(Long taskId, CreateReminderRequest reminderRequest) throws ApiException {
		return new ResponseEntity<>(taskService.createTaskReminder(taskId, reminderRequest), HttpStatus.OK);
	}
}
