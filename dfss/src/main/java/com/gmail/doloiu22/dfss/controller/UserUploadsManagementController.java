package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import com.gmail.doloiu22.dfss.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/uploads")
public class UserUploadsManagementController {

    private final String centralNodeUrl = "http://localhost:8081/upload";
    private final String centralNodeHttp = "http://localhost:8081";
    private final String centralNodeUrlDelete = "/deleteFile/{fileName}";
    private String userName = "guest";

    private final StoredFileService storedFileService;
    private final UserService userService;

    public UserUploadsManagementController(StoredFileService storedFileService, UserService userService) {
        this.storedFileService = storedFileService;
        this.userService = userService;
    }


    @GetMapping
    public String open(Model model, Authentication authentication){

        userName = authentication.getName();
        List<StoredFileEntity> listOfStoredFiles = storedFileService.getAllFilesByAuthor(userName);

        model.addAttribute("storedFiles", listOfStoredFiles);

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

    @GetMapping("/deleteFile/{fileID}")
    public String deleteFile(@PathVariable("fileID") Long fileID, HttpServletResponse response) {

        Optional<StoredFileEntity> storedFile = storedFileService.findByID(fileID);
        if (storedFile.isEmpty())
            return "error"; // TODO: Change error; file does not exist;
        String fileName = storedFile.get().getFileName();
        String author = storedFile.get().getAuthor();

        if (!userService.findByUsername(userName).get().getUsername().equals(userService.findByUsername(author).get().getUsername())) {
            // not allowed to delete; logged user is not the author
            return "error"; // TODO: Change error; logged user does not have permission to delete file
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    centralNodeHttp + centralNodeUrlDelete,
                    HttpMethod.DELETE,
                    entity,
                    Void.class,
                    fileName
            );
            storedFileService.removeFile(storedFile.get());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return "redirect:/uploads";
            } else {
                return "error"; // TODO: Change error; file couldn't be deleted
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // TODO: Change error; there was an exception thrown while deleting the file
        }
    }

    @GetMapping("/deleteFile")
    public String deleteFileButtonPressed(@RequestParam("fileID") Long fileID) {
        return "redirect:/uploads/deleteFile/" + fileID.toString();
    }

}
