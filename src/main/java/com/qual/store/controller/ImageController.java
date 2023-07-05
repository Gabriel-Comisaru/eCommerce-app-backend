package com.qual.store.controller;

import com.qual.store.model.ImageModel;
import com.qual.store.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageModel> uploadImage(@RequestParam("imageFile") MultipartFile file) {
        return ResponseEntity.ok(imageService.saveImageModel(file));
    }

}
