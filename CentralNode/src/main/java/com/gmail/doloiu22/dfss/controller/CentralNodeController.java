package com.gmail.doloiu22.dfss.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class CentralNodeController {

    @PostMapping("/upload")
    public ResponseEntity<?> handleUploadFromMainApp(@RequestParam("file") MultipartFile file) {
        String storageNodeUrl = "http://localhost:8082"; // temporary, todo: allow different urls and multiple storages

        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(convFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    storageNodeUrl + "/upload",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            convFile.delete();

            return ResponseEntity.ok("File successfully uploaded to " + storageNodeUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file to storage node.");
        }
    }

    @GetMapping("/downloadFile/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable(name = "fileName") String fileName){
        String storageNodeUrl = "http://localhost:8082" + "/downloadFile/" + fileName;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Resource> response = restTemplate.getForEntity(storageNodeUrl, Resource.class);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.getBody());
    }

    @DeleteMapping("/deleteFile/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileName") String fileName) {
        String storageNodeUrl = "http://localhost:8082" + "/deleteFile/" + fileName;

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.delete(storageNodeUrl);
            return ResponseEntity.ok("File " + fileName + " deleted successfully.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error connecting to storage service.");
        }
    }

}