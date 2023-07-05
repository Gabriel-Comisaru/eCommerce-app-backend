package com.qual.store.service.impl;

import com.qual.store.exceptions.ImageModelException;
import com.qual.store.model.ImageModel;
import com.qual.store.repository.ImageRepository;
import com.qual.store.service.ImageService;
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

    @Override
    public List<ImageModel> getAllImages() {
        return imageRepository.findAll();
    }

    @Override
    public ImageModel saveImageModel(MultipartFile file) {
        if (file == null) {
            throw new ImageModelException("No image to save");
        }

        Optional<ImageModel> image;

        try {
            System.out.println("Original Image Byte Size - " + file.getBytes().length);
            image = Optional.of(new ImageModel(file.getOriginalFilename(), file.getContentType(),
                    compressBytes(file.getBytes()), null));
        } catch (IOException e) {
            throw new ImageModelException(e.getMessage());
        }

        ImageModel imageToSave = image.orElseThrow();

        return imageRepository.save(imageToSave);
    }

    @Override
    public Optional<ImageModel> updateImageModel(Long id, ImageModel image) {
        return Optional.empty();
    }

    @Override
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
    public ImageModel findImageModelByName(String name) {
        Optional<ImageModel> retrievedImage = imageRepository.findByName(name);

        ImageModel imageModel = retrievedImage
                .orElseThrow(() -> new ImageModelException(String.format("No image with name = %s", name)));

        ImageModel result = new ImageModel(imageModel.getName(), imageModel.getType(),
                decompressBytes(imageModel.getPicByte()), null);
        result.setId(imageModel.getId());

        return result;
    }

    @Override
    public byte[] downloadImage(String fileName) {
        ImageModel dbImageData = imageRepository.findByName(fileName)
                .orElseThrow(() -> new ImageModelException(String.format("image with name = %s not found", fileName)));

        return decompressBytes(dbImageData.getPicByte());
    }

    @Override
    public void deleteImageModelById(Long id) {

    }
}
