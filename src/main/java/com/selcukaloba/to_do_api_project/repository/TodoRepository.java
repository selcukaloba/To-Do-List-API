package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
