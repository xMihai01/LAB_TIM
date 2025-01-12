package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/uploads")
public class UserUploadsManagementController {

    private final String centralNodeUrl = "http://localhost:8081/upload";
    private String userName = "guest";

    private final StoredFileService storedFileService;

    public UserUploadsManagementController(StoredFileService storedFileService) {
        this.storedFileService = storedFileService;
    }


    @GetMapping
    public String open(Model model, Authentication authentication){

        List<StoredFileEntity> listOfStoredFiles = storedFileService.getAllFiles();

        model.addAttribute("storedFiles", listOfStoredFiles);
        userName = authentication.getName();

        return "UserUploadManagement/dashboard";
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(convFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange( // TODO: use List<String> for multiple servers
                    centralNodeUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class // and new ParameterizedTypeReference<List<String>>() {}
            );

            StoredFileEntity storedFileEntity = new StoredFileEntity();
            storedFileEntity.setFileName(file.getOriginalFilename());
            storedFileEntity.setAuthor(userName);
            storedFileEntity.setPrivate(false);
            storedFileEntity.setDatePublished(LocalDateTime.now());
            storedFileEntity.setLocation("testServerUpload/" + file.getOriginalFilename());
            storedFileService.addFile(storedFileEntity);

            convFile.delete();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during file upload: " + e.getMessage());
        }

    }

}
