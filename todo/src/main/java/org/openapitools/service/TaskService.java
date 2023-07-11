package org.openapitools.service;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.weaver.ast.Var;
import org.openapitools.api.ApiException;
import org.openapitools.model.dto.CreateReminderRequest;
import org.openapitools.model.dto.TaskCreateRequest;
import org.openapitools.model.dto.TaskResponse;
import org.openapitools.model.dto.TaskUpdateRequest;
import org.openapitools.model.entity.Task;
import org.openapitools.model.mapper.TaskMapper;
import org.openapitools.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
	private final TaskRepository taskRepository;
	private final TaskMapper taskMapper;
	private final GoogleCalendarApiService googleCalendarApiService;
	private final Pattern pattern = Pattern.compile("\\|(.{2})");


	public List<TaskResponse> getAllTasksByUserId(OidcUser oidcUser) {
		Matcher matcher = pattern.matcher(Objects.requireNonNull(oidcUser.getAttribute("sub")));
		if(!matcher.find()) {
			return new ArrayList<>(10);
		}

		long id = Long.parseLong(matcher.group(1));
		List<Task> taskList = taskRepository.findAllTaskByUserId(id);
		return taskMapper.listTaskToListTaskResponse(taskList);

	}

	public TaskResponse getTaskByUserIdAndTaskId(Long taskId, OidcUser oidcUser) throws ApiException {
		Matcher matcher = pattern.matcher(Objects.requireNonNull(oidcUser.getAttribute("sub")));
		if(!matcher.find()) {
			throw new ApiException(HttpStatus.NOT_FOUND.value());
		}

		long id = Long.parseLong(matcher.group(1));
		Optional<Task> task = taskRepository.findTaskByUserIdAndId(id, taskId);
		if (task.isPresent()) {
			return taskMapper.taskToTaskResponse(task.get());
		}
		throw new ApiException(HttpStatus.NOT_FOUND.value());
	}

	public TaskResponse createTask(TaskCreateRequest taskCreateRequest, OidcUser oidcUser) throws ApiException {
		Matcher matcher = pattern.matcher(Objects.requireNonNull(oidcUser.getAttribute("sub")));
		if(!matcher.find()) {
			throw new ApiException(HttpStatus.NOT_FOUND.value());
		}

		long id = Long.parseLong(matcher.group(1));
		Optional<Task> existingTask = taskRepository.findTaskByNameAndUserId(taskCreateRequest.getName(), id);
		if (existingTask.isEmpty()) {
			Task task = new Task();
			task.setName(taskCreateRequest.getName());
			task.setUserId(id);
			task.setDueDate(taskCreateRequest.getDueDate());
			task.setCompleted(false);
			taskRepository.saveAndFlush(task);
			return taskMapper.taskToTaskResponse(task);
		} else {
			throw new ApiException(HttpStatus.CONFLICT.value());
		}
	}

	public TaskResponse updateTask(Long taskId, TaskUpdateRequest taskUpdateRequest, OidcUser oidcUser) throws ApiException {
		Matcher matcher = pattern.matcher(Objects.requireNonNull(oidcUser.getAttribute("sub")));
		if(!matcher.find()) {
			throw new ApiException(HttpStatus.NOT_FOUND.value());
		}

		long id = Long.parseLong(matcher.group(1));
		Optional<Task> existingTask = taskRepository.findTaskByUserIdAndId(id, taskId);
		if (existingTask.isPresent()) {
			existingTask.get().setName(taskUpdateRequest.getName() != null ? taskUpdateRequest.getName() : existingTask.get().getName());
			existingTask.get().setDueDate(taskUpdateRequest.getDueDate() != null ? taskUpdateRequest.getDueDate() : existingTask.get().getDueDate());
			existingTask.get()
					.setCompleted(taskUpdateRequest.getIsCompleted() != null ? taskUpdateRequest.getIsCompleted() : existingTask.get().isCompleted());
			taskRepository.saveAndFlush(existingTask.get());
			return taskMapper.taskToTaskResponse(existingTask.get());
		} else {
			throw new ApiException(HttpStatus.NOT_FOUND.value());
		}
	}

	public void deleteTask(Long taskId, OidcUser oidcUser) throws ApiException {
		Matcher matcher = pattern.matcher(Objects.requireNonNull(oidcUser.getAttribute("sub")));
		if(!matcher.find()) {
			throw new ApiException(HttpStatus.CONFLICT.value());
		}

		long id = Long.parseLong(matcher.group(1));
		Optional<Task> existingTask = taskRepository.findTaskByUserIdAndId(id, taskId);
		if (existingTask.isPresent()) {
			taskRepository.delete(existingTask.get());
		} else {
			throw new ApiException(HttpStatus.NOT_FOUND.value());
		}
	}

	public TaskResponse createTaskReminder(Long taskId, CreateReminderRequest reminderRequest) throws ApiException {
		Optional<Task> existingTask = taskRepository.findTaskById(taskId);
		if (existingTask.isPresent()) {
			try {
				googleCalendarApiService.createReminder(existingTask.get().getName(), reminderRequest.getStartDateTime(), reminderRequest.getEndDateTime());
			} catch (GeneralSecurityException e) {
				throw new ApiException(HttpStatus.UNAUTHORIZED.value());
			} catch (IOException e) {
				throw new ApiException(HttpStatus.BAD_REQUEST.value());
			}
			return taskMapper.taskToTaskResponse(existingTask.get());
		} else {
			throw new ApiException(HttpStatus.NOT_FOUND.value());
		}
	}
}
