package com.gmail.doloiu22.dfss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "username")
    private String username;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "password")
    private String password;

    @Transient
    private String repeatPassword;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserStoredFileAccessEntity> userStoredFiles;

}
