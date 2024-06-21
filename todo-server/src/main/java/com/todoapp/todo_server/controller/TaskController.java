package com.todoapp.todo_server.controller;

import com.todoapp.todo_server.entity.Roles;
import com.todoapp.todo_server.entity.Task;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.UserRepository;
import com.todoapp.todo_server.service.TaskService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/v1/tasks")
@RestController
@Log4j2
public class TaskController {
    @Autowired
    public TaskService taskService;

    @Autowired
    public UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            if(tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/search/{title}")
    public ResponseEntity<List<Task>> searchTasksByName(@PathVariable String title) {
        try {
            List<Task> tasks = taskService.getTasksByTitle(title);
            if(tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksAssignedToUser(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getTasksByUserId(userId);
            if(tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/user/{userId}")
    public ResponseEntity<Task> addTask(@RequestBody Task taskRequest, @PathVariable Long userId) {
        try {
            log.warn("Warning!, Only ADMIN role can perform this operation!");
            log.info("Adding task and assigning to user with id: {}..." , userId);
            Task newTask = userRepository.findById(userId).map(user -> {
               log.info("Assigning task to user...");
               taskRequest.setUserAssigned(user);
               return taskService.addTask(taskRequest);
            }).orElseThrow();
            log.info("Added and assigned new task id: {} to user id: {} successfully!", newTask.getId(), userId);
            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/assign/user/{userId}")
    public ResponseEntity<Task> addTask(@PathVariable Long userId, @PathVariable Long id) {
        try {
            log.warn("Warning!, Only ADMIN role can perform this operation!");
            log.info("Updating task id: {} assigning to user id: {}...", id, userId);
            Task assignedTask = taskService.getTaskById(id);
            UserEntity userAssigned = userRepository.findById(userId).get();
            assignedTask.setUserAssigned(userAssigned);
            taskService.addTask(assignedTask);
            log.info("Updated task id: {} re-assigned to user id: {} successfully!", id, userId);
            return new ResponseEntity<>(assignedTask, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update/status/{status}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @PathVariable String status) {
        try {

            log.info("Updating status of task id: {}...", id);
            Task taskUpdate = taskService.getTaskById(id);
            taskUpdate.setStatus(status);
            taskService.addTask(taskUpdate);
            log.info("Updated status: {} for task by id: {} successfully!", status, id);
            return new ResponseEntity<>(taskUpdate, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteAllTasks() {
        try{
            log.warn("Warning!, Only ADMIN role can perform this operation!");
            log.info("Deleting all tasks...");
            taskService.deleteAllTasks();
            log.info("Deleted all tasks successfully!!");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {;
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteTaskById(@PathVariable Long id) {
        try {
            log.warn("Warning!, Only ADMIN role can perform this operation!");
            log.info("Deleting task by id: {}...", id);
            taskService.deleteById(id);
            log.info("Deleted task by id: {} successfully!", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
