package com.example.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.example.app.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
{{ExtraImports}}

@Entity
@Table(name = "{{TableName}}_tbl")
public class {{ClassName}} {

{{Fields}}

    // Ownership (Standard for all entities)
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User owner;

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

{{Methods}}
}