package com.todoapp.todo_server.controller;

import com.todoapp.todo_server.entity.Roles;
import com.todoapp.todo_server.entity.Task;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.TaskRepository;
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
            log.error(e);
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
            log.error(e);
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
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        }catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/user/{userId}")
    public ResponseEntity<Task> addTask(@RequestBody Task taskRequest, @PathVariable Long userId) {
        try {
            Task newTask = taskService.addTaskAndAssign(taskRequest, userId);
            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        }catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/assign/user/{userId}")
    public ResponseEntity<Task> addTask(@PathVariable Long userId, @PathVariable Long id) {
        try {
            Task assignedTask = taskService.assignTaskToUser(userId, id);
            return new ResponseEntity<>(assignedTask, HttpStatus.OK);
        }catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update/status/{status}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @PathVariable String status) {
        try {
            Task updatedTaskStatus = taskService.updateTaskStatus(id, status);
            return new ResponseEntity<>(updatedTaskStatus, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteAllTasks() {
        try{
            taskService.deleteAllTasks();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {;
            log.error(e);
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
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
