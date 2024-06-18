package com.todoapp.todo_server.controller;

import com.todoapp.todo_server.entity.Roles;
import com.todoapp.todo_server.entity.Task;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.UserRepository;
import com.todoapp.todo_server.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/v1/tasks")
@RestController

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


    @GetMapping("/user/{userId}")
    public ResponseEntity<Optional<List<Task>>> getTasksAssignedToUser(@PathVariable Long userId) {
        try {
            Optional<List<Task>> tasks = taskService.getTasksByUserId(userId);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            Optional<Task> taskData = taskService.getTaskById(id);
            return taskData.map(task -> new ResponseEntity<>(task, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));

        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/user/{userId}")
    public ResponseEntity<Task> addTask(@RequestBody Task taskRequest, @PathVariable Long userId) {
        try {
            Task newTask = userRepository.findById(userId).map(user -> {
               taskRequest.setUserAssigned(user);
               return taskService.addTask(taskRequest);
            }).orElseThrow();
            System.out.println(newTask.getId());
            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/assign/user/{userId}")
    public ResponseEntity<Task> addTask(@PathVariable Long userId, @PathVariable Long id) {
        try {
            Optional<Task> task = taskService.getTaskById(id);
            Optional<UserEntity> userAssigned = userRepository.findById(userId);
            Task assignedTask = task.get();
            assignedTask.setUserAssigned(userAssigned.get());
            taskService.addTask(assignedTask);
            System.out.println(assignedTask.getUserAssigned().getId());
            return new ResponseEntity<>(assignedTask, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteAllTasks() {
        try{
            taskService.deleteAllTasks();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteTaskById(@PathVariable Long id) {
        try {
            taskService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
