package com.freightfox.file.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freightfox.file.service.FileService;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFiles(
            @RequestParam String userName,
            @RequestParam String searchTerm) {
        List<String> files = fileService.searchFilesList(userName, searchTerm);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
