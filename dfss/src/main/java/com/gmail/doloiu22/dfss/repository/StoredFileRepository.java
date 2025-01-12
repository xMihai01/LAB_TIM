package com.gmail.doloiu22.dfss.repository;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
public interface StoredFileRepository extends JpaRepository<StoredFileEntity, Long> {

    Optional<StoredFileEntity> findById(long id);
    List<StoredFileEntity> findAllByAuthor(String author);

}
