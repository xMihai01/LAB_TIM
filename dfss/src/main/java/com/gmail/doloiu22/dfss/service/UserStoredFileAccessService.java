package com.gmail.doloiu22.dfss.service;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.model.UserEntity;
import com.gmail.doloiu22.dfss.model.UserStoredFileAccessEntity;
import com.gmail.doloiu22.dfss.repository.UserStoredFileAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Configuration
@RequiredArgsConstructor
public class UserStoredFileAccessService {


    private final UserStoredFileAccessRepository userStoredFileAccessRepository;

    public void allowAccess(UserEntity user, StoredFileEntity storedFile) {

        UserStoredFileAccessEntity userStoredFileAccess = new UserStoredFileAccessEntity();

        userStoredFileAccess.setUser(user);
        userStoredFileAccess.setStoredFile(storedFile);

        userStoredFileAccessRepository.save(userStoredFileAccess);
    }

    public void revokeAccess(UserEntity user, StoredFileEntity storedFile) {

        Optional<UserStoredFileAccessEntity> userStoredFileAccess = userStoredFileAccessRepository.findAllByUserAndStoredFile(user, storedFile);

        if (userStoredFileAccess.isEmpty()) {
            return; // TODO: throw exception
        }

        userStoredFileAccessRepository.delete(userStoredFileAccess.get());

    }

    public List<UserEntity> findAllUserAccessForFile(StoredFileEntity storedFileEntity) {

        List<UserStoredFileAccessEntity> storedFileAccessEntities = userStoredFileAccessRepository.findAllByStoredFile(storedFileEntity);
        List<UserEntity> userEntities = new LinkedList<>();

        storedFileAccessEntities.stream()
                .map(UserStoredFileAccessEntity::getUser)
                .forEach(userEntities::add);

        return userEntities;
    }

    public List<StoredFileEntity> findAllFilesForUserAccess(UserEntity userEntity) {

        List<UserStoredFileAccessEntity> storedFileAccessEntities = userStoredFileAccessRepository.findAllByUser(userEntity);
        List<StoredFileEntity> storedFileEntities = new LinkedList<>();

        storedFileAccessEntities.stream()
                .map(UserStoredFileAccessEntity::getStoredFile)
                .forEach(storedFileEntities::add);
        return storedFileEntities;
    }

}
