package com.gmail.doloiu22.dfss.util;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import com.gmail.doloiu22.dfss.service.UserService;

import java.util.Optional;

public class ValidationsUtil {


    private final UserService userService;
    private final StoredFileService storedFileService;

    public ValidationsUtil(UserService userService, StoredFileService storedFileService) {
        this.userService = userService;
        this.storedFileService = storedFileService;
    }

    public boolean isUserTheAuthor(String username, StoredFileEntity storedFileEntity) {

        String author = storedFileEntity.getAuthor();

        return userService.findByUsername(username).get().getUsername().equals(userService.findByUsername(author).get().getUsername());
    }

}
