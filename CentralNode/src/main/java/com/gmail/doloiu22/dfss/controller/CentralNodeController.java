package com.gmail.doloiu22.dfss.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CentralNodeController {

    @PostMapping("/upload")
    public ResponseEntity<?> handleUploadFromMainApp(@RequestParam("file") MultipartFile file) {
        String storageNodeUrl = "http://localhost:8082"; // temporary, todo: allow different urls and multiple storages

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<Resource> requestEntity =
                    new HttpEntity<>(file.getResource(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    storageNodeUrl + "/upload", HttpMethod.POST, requestEntity, String.class);


            return ResponseEntity.ok("File successfully forwarded to " + storageNodeUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file to storage node.");
        }
    }
}