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
            throw new Exception("Error found!"+ e);
        }
    }

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public Optional<List<Task>> getTasksByUserId(Long userId) {
        Optional<UserEntity> userAssigned = userRepository.findById(userId);
        return taskRepository.findAllByUserAssigned(userAssigned);
    }

    public List<Task> getTasksByTitle(String title) {

        return taskRepository.findAll().stream()
                .filter(task -> task.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
}
