package com.rhytham.imageConverter.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageUtil {

    // Converts an image from a URL to WebP and returns the bytes.
    public byte[] convertImageFromUrl(String imageUrl) throws IOException, InterruptedException {
        String rawFileName = Paths.get(new URL(imageUrl).getPath())
                .getFileName()
                .toString()
                .toLowerCase();

        Path tempInput = Files.createTempFile("img-", ".tmp");

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, tempInput, StandardCopyOption.REPLACE_EXISTING);
        }

        long sizeKB = Files.size(tempInput) / 1024;

        if (rawFileName.endsWith(".webp") && sizeKB <= 200) {
            System.out.println("Already WebP and <= 200KB. Skipping conversion.");
            return Files.readAllBytes(tempInput);
        } else if (rawFileName.endsWith(".png")) {
            return compressToWebP(tempInput, "lossless");
        } else {
            return compressToWebP(tempInput, "80");
        }
    }

    // Converts an image from a local file path (used for multipart uploads).
    public byte[] convertImageFromPath(Path inputPath, String quality) throws IOException, InterruptedException {
        return compressToWebP(inputPath, quality);
    }

    private byte[] compressToWebP(Path tempInput, String quality) throws IOException, InterruptedException {
        Path tempOutput = Files.createTempFile("img-", ".webp");

        List<String> command = new ArrayList<>();
        command.add(extractCwebpExecutable());
        command.add(tempInput.toAbsolutePath().toString());

        if ("lossless".equalsIgnoreCase(quality)) {
            command.add("-lossless");
        } else {
            command.add("-q");
            command.add(quality);
        }

        command.add("-o");
        command.add(tempOutput.toAbsolutePath().toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || !Files.exists(tempOutput)) {
            throw new RuntimeException("WebP conversion failed for: " + tempInput);
        }

        byte[] result = Files.readAllBytes(tempOutput);

        Files.deleteIfExists(tempInput);
        Files.deleteIfExists(tempOutput);

        return result;
    }

    // Extracts cwebp.exe from resources to a temporary file and returns its path.
    private String extractCwebpExecutable() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("static/cwebp.exe");
        if (in == null) {
            throw new FileNotFoundException("cwebp.exe not found in resources/static/");
        }

        Path tempFile = Files.createTempFile("cwebp-", ".exe");
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        tempFile.toFile().setExecutable(true);
        return tempFile.toAbsolutePath().toString();
    }
}
