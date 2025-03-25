package com.skillbox.auth.controller;

import com.skillbox.auth.service.CaptchaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageController {

    private final CaptchaServiceImpl captchaService;
    @Value("${image.uploadDirectory}")
    String imageDirectory;

    @GetMapping("/{imageId}")
    public byte[] getImages(@PathVariable UUID imageId) throws IOException {


        return captchaService.getImage(imageDirectory, imageId.toString());
    }
}
