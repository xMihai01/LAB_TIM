package com.gmail.doloiu22.dfss.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class StorageNodeController {

    private String uploadDir = "testServerUpload/";

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return new ResponseEntity<>("File uploaded successfully: " + file.getOriginalFilename(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload the file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/downloadFile/{fileName}")
    public InputStreamResource downloadFile(HttpServletResponse response, @PathVariable(name="fileName") String fileName) throws IOException {

        String filePath = uploadDir + fileName;

        response.setContentType("application/file");
        response.setHeader("Content-Disposition","attachment; filename=" + fileName);

        return new InputStreamResource(new FileInputStream(filePath));
    }

    @DeleteMapping(value = "/deleteFile/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable(name = "fileName") String fileName){
        String filePath = uploadDir + fileName;
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                return ResponseEntity.ok().body("Successfully deleted file!");
            } else {
                return ResponseEntity.internalServerError().body("Error deleting file! Could not delete file from the filesystem.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting file! File not found.");
        }
    }
}
