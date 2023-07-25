package com.qual.store.service;

import com.qual.store.model.ImageModel;
import com.qual.store.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    List<ImageModel> getAllImages();

    Product saveImageModel(MultipartFile file, Long productId);

    Optional<ImageModel> updateImageModel(Long id, ImageModel image);

    ImageModel findImageModelById(Long id);

    ImageModel findImageModelByName(String name);

    byte[] downloadImage(String fileName);

    void deleteImageModelByName(String imageName);
}
