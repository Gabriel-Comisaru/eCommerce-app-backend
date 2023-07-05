package com.qual.store.controller;

import com.qual.store.converter.ImageConverter;
import com.qual.store.dto.ImageModelDto;
import com.qual.store.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ImageConverter imageConverter;

    @PostMapping("/upload")
    public ResponseEntity<ImageModelDto> uploadImage(@RequestParam("imageFile") MultipartFile file) {
        return ResponseEntity.ok(imageConverter.convertModelToDto(imageService.saveImageModel(file)));
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadImageByName(@RequestParam("name") String imageName) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageService.downloadImage(imageName));
    }

    @GetMapping("/getById")
    public ResponseEntity<ImageModelDto> getImageById(@RequestParam("id") Long imageId) {
        return ResponseEntity.ok(imageConverter.convertModelToDto(imageService.findImageModelById(imageId)));
    }

    @GetMapping("/getByName")
    public ResponseEntity<ImageModelDto> getImageByName(@RequestParam("name") String imageName) {
        return ResponseEntity.ok(imageConverter.convertModelToDto(imageService.findImageModelByName(imageName)));
    }
}
