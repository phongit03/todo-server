package com.todoapp.todo_server.service;

import com.todoapp.todo_server.entity.Task;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.TaskRepository;
import com.todoapp.todo_server.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public Task getTaskById(Long id) throws Exception {

        try {
            log.info("Finding task with id: {}...", id);
            Optional<Task> taskById = taskRepository.findById(id);
            if(taskById.isPresent()) {
                log.info("Found task with id: {}!", id);
                return taskById.get();
            }
            throw new Exception("User with id " + id + " not found!");
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
}
