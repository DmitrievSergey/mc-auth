package com.skillbox.auth.service;

import com.skillbox.auth.entity.CaptchaEntity;

import java.io.IOException;

public interface CaptchaService {
    String saveImageToStorage(String imageDirectory, String uniqueFileName) throws IOException;

    byte[] getImage(String imageDirectory, String imageName) throws IOException;

    String deleteImage(String imageDirectory, String imageName) throws IOException;

    CaptchaEntity getCaptchaById(String id);
}
