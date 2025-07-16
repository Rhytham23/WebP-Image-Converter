package com.rhytham.imageConverter.controller;

import com.rhytham.imageConverter.model.ImageRequest;
import com.rhytham.imageConverter.service.ImageConvertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageConvertService imageConvertService;

    public ImageController(ImageConvertService imageConvertService) {
        this.imageConvertService = imageConvertService;
    }

    //JSON-based (URL)
    @PostMapping("/convert")
    public ResponseEntity<String> convertImages(@RequestBody ImageRequest imageRequest) {
        imageConvertService.convertAndSaveImages(imageRequest.getImageUrls());
        return ResponseEntity.ok("Image URLs converted and saved successfully.");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        imageConvertService.convertUploadedImages(files);
        return ResponseEntity.ok("Images converted and saved successfully in 'converted-images' folder.");
    }


}
