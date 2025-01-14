package com.gmail.doloiu22.dfss.repository;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.model.UserEntity;
import com.gmail.doloiu22.dfss.model.UserStoredFileAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStoredFileAccessRepository extends JpaRepository<UserStoredFileAccessEntity, Long> {

    Optional<UserStoredFileAccessEntity> findAllByUserAndStoredFile(UserEntity user, StoredFileEntity storedFile);
    List<UserStoredFileAccessEntity> findAllByStoredFile(StoredFileEntity storedFileEntity);
    List<UserStoredFileAccessEntity> findAllByUser(UserEntity userEntity);

}
