package com.qual.store.service.impl;

import com.qual.store.exceptions.ImageModelException;
import com.qual.store.model.ImageModel;
import com.qual.store.repository.ImageRepository;
import com.qual.store.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
                    compressBytes(file.getBytes())));
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
        return null;
    }

    @Override
    public void deleteImageModelById(Long id) {

    }

    // compress the image bytes before storing it in the database
    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }

    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ioe) {
        }
        return outputStream.toByteArray();
    }
}
