package com.selcukaloba.to_do_api_project.Repository;

import com.selcukaloba.to_do_api_project.Entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
