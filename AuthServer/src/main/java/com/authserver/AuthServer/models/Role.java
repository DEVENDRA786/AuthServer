package com.authserver.AuthServer.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Table(schema = "role")
public class Role extends BaseModel {
    private String name;
}
