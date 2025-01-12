package com.gmail.doloiu22.dfss.service;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Configuration
@RequiredArgsConstructor
public class StoredFileService {

    private final StoredFileRepository storedFileRepository;

    public Optional<StoredFileEntity> findByID(long id) {
        return storedFileRepository.findById(id);
    }

    public List<StoredFileEntity> getAllFiles() {
        return storedFileRepository.findAll();
    }
    public List<StoredFileEntity> getAllFilesByAuthor(String author) {return storedFileRepository.findAllByAuthor(author);}

    public void addFile(StoredFileEntity storedFileEntity) {
        storedFileRepository.save(storedFileEntity);
    }
    public void removeFile(StoredFileEntity storedFileEntity) {storedFileRepository.delete(storedFileEntity);}

}
