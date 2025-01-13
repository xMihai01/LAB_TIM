package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.model.UserEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import com.gmail.doloiu22.dfss.service.UserService;
import com.gmail.doloiu22.dfss.util.ValidationsUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/uploads/manageFileAccess")
public class ManageFileController {

    private final UserService userService;
    private final StoredFileService storedFileService;
    private final ValidationsUtil validationsUtil;

    public ManageFileController(UserService userService, StoredFileService storedFileService) {
        this.userService = userService;
        this.storedFileService = storedFileService;
        validationsUtil= new ValidationsUtil(userService, storedFileService);
    }

    @GetMapping("/{fileID}")
    public String openManageFileAccess(@PathVariable("fileID") Long fileID, Model model, Authentication authentication) {

        Optional<StoredFileEntity> storedFile = storedFileService.findByID(fileID);

        if (storedFile.isEmpty())
            return "error"; // TODO: change error; file does not exist

        if (!validationsUtil.isUserTheAuthor(authentication.getName(), storedFile.get()))
            return "error"; // TODO: change error; user is not the author of the file

        List<UserEntity> userEntityList = userService.findAll(); // TODO: change list; it should only contain users that got access to the file

        model.addAttribute("users", userEntityList);

        return "UserUploadManagement/manageFile";
    }

}
