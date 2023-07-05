package com.qual.store.service;

import com.qual.store.model.ImageModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    List<ImageModel> getAllImages();

    ImageModel saveImageModel(MultipartFile file);

    Optional<ImageModel> updateImageModel(Long id, ImageModel image);

    ImageModel findImageModelById(Long id);

    ImageModel findImageModelByName(String name);

    void deleteImageModelById(Long id);
}
