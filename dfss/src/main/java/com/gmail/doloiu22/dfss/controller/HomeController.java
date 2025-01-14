package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import com.gmail.doloiu22.dfss.service.UserService;
import com.gmail.doloiu22.dfss.service.UserStoredFileAccessService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final String centralNodeHttp = "http://localhost:8081";
    private final String centralNodeUrlDownload = "/downloadFile/{fileName}";
    private String userName = "unknown";

    @Autowired
    private StoredFileService storedFileService;
    @Autowired
    private UserStoredFileAccessService userStoredFileAccessService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String open(Model model, Authentication authentication){

        List<StoredFileEntity> listOfStoredFiles = storedFileService.getAllFilesByPrivacy(false);

        model.addAttribute("storedFiles", listOfStoredFiles);
        userName = authentication.getName();

        return "home/dashboard";
    }

    @GetMapping("/privateUploads")
    public String openPrivateUploads(Model model, Authentication authentication){

        List<StoredFileEntity> listOfStoredFiles = userStoredFileAccessService.findAllFilesForUserAccess(userService.findByUsername(authentication.getName()).get());

        model.addAttribute("storedFiles", listOfStoredFiles);
        userName = authentication.getName();

        return "home/private_uploads";
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("storedFileID") Long fileID, HttpServletResponse response) {

        // TODO: Check if Optional is null or not (if file was found in the db)
        // TODO: Change parameter in central node from String fileName to Long fileID

        Optional<StoredFileEntity> optionalFile = storedFileService.findByID(fileID);

        RestTemplate restTemplate = new RestTemplate();
        URI url = UriComponentsBuilder.fromHttpUrl(centralNodeHttp)
                .path(centralNodeUrlDownload)
                .buildAndExpand(optionalFile.get().getFileName())
                .toUri();

        ResponseEntity<Resource> res = restTemplate.getForEntity(url, Resource.class);
        String fileName = optionalFile.get().getFileName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(res.getBody());
    }
}
