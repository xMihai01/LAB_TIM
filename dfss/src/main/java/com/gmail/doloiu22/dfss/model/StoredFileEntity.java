package com.gmail.doloiu22.dfss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Getter @Setter
@Entity
@Table(name="stored_files")
public class StoredFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "file_name")
    private String fileName;

    @Basic
    @Column(name = "author")
    private String author;

    @Basic
    @Column(name = "date_published")
    private LocalDateTime datePublished;

    @Basic
    @Column(name = "isPrivate")
    private boolean isPrivate;

    @Basic
    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "storedFile", cascade = CascadeType.ALL)
    private Set<UserStoredFileAccessEntity> userStoredFiles;

    public String getDatePublishedAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return datePublished.format(formatter);
    }
}
