package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.model.UserEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import com.gmail.doloiu22.dfss.service.UserService;
import com.gmail.doloiu22.dfss.service.UserStoredFileAccessService;
import com.gmail.doloiu22.dfss.util.ValidationsUtil;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/uploads/manageFileAccess")
public class ManageFileController {

    private Long fileID;
    private String username;

    private final UserService userService;
    private final StoredFileService storedFileService;
    private final UserStoredFileAccessService userStoredFileAccessService;
    private final ValidationsUtil validationsUtil;

    public ManageFileController(UserService userService, StoredFileService storedFileService, UserStoredFileAccessService userStoredFileAccessService) {
        this.userService = userService;
        this.storedFileService = storedFileService;
        this.userStoredFileAccessService = userStoredFileAccessService;
        validationsUtil= new ValidationsUtil(userService, storedFileService);
    }

    @GetMapping("/{fileID}")
    public String openManageFileAccess(@PathVariable("fileID") Long fileID, Model model, Authentication authentication) {

        Optional<StoredFileEntity> storedFile = storedFileService.findByID(fileID);
        username = authentication.getName();

        if (storedFile.isEmpty())
            return "errors/file_does_not_exist";

        if (!validationsUtil.isUserTheAuthor(username, storedFile.get()))
            return "errors/missing_permission";

        List<UserEntity> userEntityList = userStoredFileAccessService.findAllUserAccessForFile(storedFile.get());
        this.fileID = fileID;

        model.addAttribute("users", userEntityList);

        return "UserUploadManagement/manageFile";
    }

    @PostMapping("/allowAccess")
    public String submitAccessButtonPressed(@RequestParam("submittedUsername") String submittedUsername) {

        Optional<UserEntity> user = userService.findByUsername(submittedUsername);
        Optional<StoredFileEntity> storedFile = storedFileService.findByID(fileID);

        if (!submittedUsername.isEmpty() && (user.isPresent() && storedFile.isPresent())) {
            userStoredFileAccessService.allowAccess(user.get(), storedFile.get());
        }

        return "redirect:/uploads/manageFileAccess/" + fileID.toString();
    }

    @DeleteMapping("/revokeAccess")
    public String revokeAccessButtonPressed(@RequestParam("userID") Long userID) {

        Optional<UserEntity> user = userService.findById(userID);
        Optional<StoredFileEntity> storedFile = storedFileService.findByID(fileID);

        if (user.isPresent() && storedFile.isPresent()) {
            userStoredFileAccessService.revokeAccess(user.get(), storedFile.get());
        }

        return "redirect:/uploads/manageFileAccess/" + fileID.toString();
    }

}
