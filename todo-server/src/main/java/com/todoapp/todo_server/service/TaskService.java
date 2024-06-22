package com.todoapp.todo_server.service;

import com.todoapp.todo_server.entity.Task;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.TaskRepository;
import com.todoapp.todo_server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TaskService {
    @Autowired
    public TaskRepository taskRepository;

    @Autowired
    public UserRepository userRepository;

    public List<Task> getAllTasks() throws Exception {
        try {
            log.info("Fetching all tasks...");
            List<Task> tasks = taskRepository.findAll();
            log.info("{} Tasks Found!", tasks.size());
            return tasks;
        } catch (Exception e) {
            throw new Exception("Error found in getAll service:"+ e);
        }
    }

    public Task addTaskAndAssign(Task task, Long userId) throws Exception {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found!"));
            task.setUserAssigned(user);
            return taskRepository.save(task);
        } catch (Exception e) {
            throw new Exception("Error found in add & assign new task service: "+ e);
        }

    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public Task getTaskById(Long id) throws Exception {
        try {
            log.info("Finding task with id: {}...", id);
            return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task With Id " + id + " Not Found!"));
        } catch (Exception e) {
            throw new Exception("Error found in getTaskById service: " + e);
        }
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByUserId(Long userId) throws Exception {
        try {
            log.info("Fetching tasks for user with id: {}...", userId);
            Optional<UserEntity> userAssigned = userRepository.findById(userId);
            if (userAssigned.isPresent()) {
                log.info("User with id {} found!", userAssigned.get().getId());
                List<Task> tasksById = taskRepository.findAllByUserAssigned(userAssigned.get());
                log.info("{} Tasks Found For User With Id {}", tasksById.size(), userId);
                return tasksById;
            }
            throw new Exception("User with Id " + userId + " Not Found!");
        } catch (Exception e) {
            throw new Exception("Error found in getByUserId service: " + e);
        }

    }

    public List<Task> getTasksByTitle(String title) throws Exception {
        try {
            log.info("Fetching tasks by title...");
            List<Task> tasksByTitle = taskRepository.findAll().stream()
                    .filter(task -> task.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
            log.info("{} Tasks Found by title!", tasksByTitle.size());
            return tasksByTitle;
        } catch (Exception e) {
            throw new Exception("Error found in getTasksByTitle service: " + e);
        }

    }

    public Task assignTaskToUser(Long userId, Long id) throws Exception {
        try {
            log.info("Finding user with Id {}...", userId);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User With Id: "+userId+" Not Found!"));
            log.info("Finding Task with Id {}...", id);
            Task assignedTask = taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Task By Id: " + id + " Not Found!"));
            assignedTask.setUserAssigned(user);
            log.info("Assigned Task by Id {} to User by Id {}", id, userId);
            return taskRepository.save(assignedTask);
        } catch (Exception e) {
            throw new Exception("Error found in Assign Task to User service: "+e);
        }
    }

    public Task updateTaskStatus(Long id, String status) throws Exception {
        try {
            log.info("Finding Task By Id {}...", id);
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Task By Id "+id+" Not Found!"));
            task.setStatus(status);
            log.info("Updated Task Status {}", status);
            return taskRepository.save(task);
        } catch (Exception e) {
            throw new Exception("Error found in updateTaskStatus service: "+e);
        }
    }
}
