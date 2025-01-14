package com.gmail.doloiu22.dfss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name="user_stored_file")
public class UserStoredFileAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "storedFileID", referencedColumnName = "id")
    private StoredFileEntity storedFile;

}
