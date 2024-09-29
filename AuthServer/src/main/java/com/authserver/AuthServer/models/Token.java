package com.authserver.AuthServer.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(schema = "token")
public class Token extends BaseModel {
    private String value;
    @ManyToOne
    private User user;
    private Date expiryAt;
}
