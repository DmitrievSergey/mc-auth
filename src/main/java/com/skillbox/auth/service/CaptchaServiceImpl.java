package com.skillbox.auth.service;

import com.github.cage.Cage;
import com.github.cage.YCage;
import com.skillbox.auth.entity.CaptchaEntity;
import com.skillbox.auth.repository.CaptchaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaRepository captchaRepository;

    public String saveImageToStorage(String uploadDirectory, String uniqueFileName) throws IOException {
        //String uniqueFileName = UUID.randomUUID().toString() + "." + "jpg";

        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Cage currGCage = new YCage();
        String captchaToken = currGCage.getTokenGenerator().next();

        try {
            OutputStream os = new FileOutputStream(String.valueOf(filePath), false);
            currGCage.draw(captchaToken, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        captchaRepository.save(new CaptchaEntity(uniqueFileName, captchaToken));

        return uniqueFileName;
    }

    public byte[] getImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        } else {
            return null; // Handle missing images
        }
    }

    public String deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return "Success";
        } else {
            return "Failed"; // Handle missing images
        }
    }

    @Override
    public CaptchaEntity getCaptchaById(String id) {
        return captchaRepository.findById(id).orElseThrow();
    }
}
