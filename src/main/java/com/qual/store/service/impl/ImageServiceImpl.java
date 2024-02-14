package com.qual.store.service.impl;

import com.qual.store.exceptions.ImageModelException;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.ImageModel;
import com.qual.store.model.Product;
import com.qual.store.repository.ImageRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.qual.store.utils.images.ImageUtils.compressBytes;
import static com.qual.store.utils.images.ImageUtils.decompressBytes;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Override
    @Log
    public List<ImageModel> getAllImages() {
        return imageRepository.findAll();
    }

    @Override
    @Log
    @Transactional
    public Product saveImageModel(MultipartFile file, Long productId) {
        if (file == null) {
            throw new ImageModelException("No image to save");
        }

        Product existingProduct = productRepository.findAllWithCategoryAndReviewsAndImages()
                .stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("product with id = %s not found", productId)
                ));

        Optional<ImageModel> image;

        try {
            System.out.println("Original Image Byte Size - " + file.getBytes().length);
            image = Optional.of(new ImageModel(file.getOriginalFilename(), file.getContentType(),
                    compressBytes(file.getBytes()), existingProduct));
        } catch (IOException e) {
            throw new ImageModelException(e.getMessage());
        }

        ImageModel imageToSave = image.orElseThrow();

        existingProduct.addImageModel(imageToSave);
        return productRepository.save(existingProduct);
    }

    @Override
    @Log
    public Optional<ImageModel> updateImageModel(Long id, ImageModel image) {
        return Optional.empty();
    }

    @Override
    @Log
    public ImageModel findImageModelById(Long id) {
        Optional<ImageModel> retrievedImage = imageRepository.findById(id);

        ImageModel imageModel = retrievedImage
                .orElseThrow(() -> new ImageModelException(String.format("No image with id = %s", id)));

        ImageModel result = new ImageModel(imageModel.getName(), imageModel.getType(),
                decompressBytes(imageModel.getPicByte()), null);
        result.setId(imageModel.getId());

        return result;
    }

    @Override
    @Log
    public ImageModel findImageModelByName(String name) {
        Optional<ImageModel> retrievedImage = imageRepository.findByName(name);

        ImageModel imageModel = retrievedImage
                .orElseThrow(() -> new ImageModelException(String.format("No image with name = %s", name)));

        ImageModel result = new ImageModel(imageModel.getName(), imageModel.getType(),
                decompressBytes(imageModel.getPicByte()), imageModel.getProduct());
        result.setId(imageModel.getId());

        return result;
    }

    @Override
    @Log
    public byte[] downloadImage(String fileName) {
        ImageModel dbImageData = imageRepository.findByName(fileName)
                .orElseThrow(() -> new ImageModelException(String.format("image with name = %s not found", fileName)));

        return decompressBytes(dbImageData.getPicByte());
    }

    @Override
    @Log
    public void deleteImageModelByName(String imageName) {
        ImageModel dbImageData = imageRepository.findByName(imageName)
                .orElseThrow(() -> new ImageModelException(String.format("image with name = %s not found", imageName)));


        Product product = productRepository.findAllWithCategoryAndReviewsAndImages()
                .stream().filter(prod -> prod.getId().equals(dbImageData.getProduct().getId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("product not found"));

        productRepository.save(product);

        imageRepository.deleteById(dbImageData.getId());
    }
}
