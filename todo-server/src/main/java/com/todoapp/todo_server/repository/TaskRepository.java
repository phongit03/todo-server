package com.todoapp.todo_server.repository;

import com.todoapp.todo_server.entity.Task;
import com.todoapp.todo_server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<List<Task>> findAllByUserAssigned(Optional<UserEntity> user);

}
