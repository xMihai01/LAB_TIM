package com.gmail.doloiu22.dfss.controller;

import com.gmail.doloiu22.dfss.model.StoredFileEntity;
import com.gmail.doloiu22.dfss.service.StoredFileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final String centralNodeUrl = "http://localhost:8081/upload";
    private final String centralNodeHttp = "http://localhost:8081";
    private final String centralNodeUrlDownload = "/downloadFile/{fileName}";
    private String userName = "unknown";

    @Autowired
    private StoredFileService storedFileService;

    @GetMapping
    public String open(Model model, Authentication authentication){

        List<StoredFileEntity> listOfStoredFiles = storedFileService.getAllFiles();

        model.addAttribute("storedFiles", listOfStoredFiles);
        userName = authentication.getName();

        return "home/dashboard";
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

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("storedFileID") Long fileID, HttpServletResponse response) {

        // TODO: Check if Optional is null or not (if file was found in the db)

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
