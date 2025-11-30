package com.example.app.controller;

import com.example.app.entity.{{ClassName}};
import com.example.app.repository.{{ClassName}}Repository;
import com.example.app.user.User;
import com.example.app.user.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@RestController
@RequestMapping("/api/{{TableName}}")
public class {{ClassName}}Controller {

    private final {{ClassName}}Repository repository;
    private final UserRepository userRepository;

    public {{ClassName}}Controller({{ClassName}}Repository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

{{ControllerMethods}}
}