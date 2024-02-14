package com.qual.store.controller;

import com.qual.store.converter.ImageConverter;
import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.ImageModelDto;
import com.qual.store.dto.MessageResponse;
import com.qual.store.dto.ProductDto;
import com.qual.store.logger.Log;
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
    private final ProductConverter productConverter;

    @Log
    @PostMapping("/upload/{productId}")
    public ResponseEntity<ProductDto> uploadImage(@RequestParam("imageFile") MultipartFile file,
                                                  @PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productConverter.convertModelToDto(imageService.saveImageModel(file, productId)));
    }

    @Log
    @GetMapping("/download")
    public ResponseEntity<?> downloadImageByName(@RequestParam("name") String imageName) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageService.downloadImage(imageName));
    }

    @Log
    @GetMapping("/getById")
    public ResponseEntity<ImageModelDto> getImageById(@RequestParam("id") Long imageId) {
        return ResponseEntity.ok(imageConverter.convertModelToDto(imageService.findImageModelById(imageId)));
    }

    @Log
    @GetMapping("/getByName")
    public ResponseEntity<ImageModelDto> getImageByName(@RequestParam("name") String imageName) {
        return ResponseEntity.ok(imageConverter.convertModelToDto(imageService.findImageModelByName(imageName)));
    }

    @Log
    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponse> deleteImageByName(@RequestParam("name") String imageName) {
        imageService.deleteImageModelByName(imageName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .message(String.format("Image with id %s deleted successfully.", imageName))
                        .build()
                );
    }
}
