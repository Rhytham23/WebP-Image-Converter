package com.rhytham.imageConverter.service;

import com.rhytham.imageConverter.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@Service
public class ImageConvertService {

    private final ImageUtil imageUtil;

    @Value("${output.path}")
    private String outputDir;

    public ImageConvertService(ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
    }

    // Convert images from a list of URLs
    public void convertAndSaveImages(List<String> imageUrls) {
        int i = 1;
        for (String url : imageUrls) {
            try {
                byte[] webpImage = imageUtil.convertImageFromUrl(url);
                String outputName = getCleanFileNameFromUrl(url);
                String path = Paths.get(outputDir, outputName).toString();

                try (FileOutputStream fos = new FileOutputStream(path)) {
                    fos.write(webpImage);
                }

                System.out.println("Converted from URL and saved: " + path);
                i++;
            } catch (Exception e) {
                System.err.println("Failed to convert from URL: " + url);
                e.printStackTrace();
            }
        }
    }

    // Convert uploaded images (multipart files)
    public void convertUploadedImages(List<MultipartFile> files) {
        try {
            Path outputDirPath = Paths.get("converted-images");
            Files.createDirectories(outputDirPath); // Ensure folder exists

            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                String outputName = cleanFileName(originalName);

                Path tempInput = Files.createTempFile("upload-", ".tmp");
                file.transferTo(tempInput.toFile());

                byte[] webpBytes = imageUtil.convertImageFromPath(tempInput, "80");

                // Save the converted file
                Path outputPath = outputDirPath.resolve(outputName);
                try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                    fos.write(webpBytes);
                }

                System.out.println("✅ Saved: " + outputName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert images");
        }
    }

    private String getCleanFileNameFromUrl(String url) {
        try {
            String original = Paths.get(new URL(url).getPath())
                    .getFileName()
                    .toString();

            return cleanFileName(original);
        } catch (Exception e) {
            return "converted-" + System.currentTimeMillis() + ".webp";
        }
    }

    private String cleanFileName(String originalName) {
        if (originalName == null) return "converted-image.webp";

        String nameWithoutExtension = originalName.replaceAll("\\.[^.]+$", "");
        String cleaned = nameWithoutExtension.trim()
                .replaceAll("[\\s_]+", "-")
                .replaceAll("[^a-zA-Z0-9\\-]", "");

        return cleaned + ".webp";
    }


}