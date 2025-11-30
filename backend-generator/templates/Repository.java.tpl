package com.example.app.repository;

import com.example.app.entity.{{ClassName}};
import com.example.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface {{ClassName}}Repository extends JpaRepository<{{ClassName}}, Long> {
    List<{{ClassName}}> findByOwner(User owner);
}