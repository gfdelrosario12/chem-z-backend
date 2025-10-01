package com.chemz.lms.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class S3Service {
    // --- New Public Constants for Segregation ---
    public static final String FOLDER_DOCUMENTS = "documents";
    public static final String FOLDER_PROOFS = "proofs";
    // ------------------------------------------

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName = "chemz";

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String getBucketName() {
        return bucketName;
    }

    /**
     * Generates a presigned URL for uploading a file to a specific S3 folder (prefix).
     * @param folderName The S3 folder (prefix) to upload to (e.g., "proofs", "documents").
     * @param fileName The name of the file (e.g., "screenshot_123.png").
     * @param contentType The MIME type (e.g., "image/png").
     * @return The presigned URL string.
     */
    public String generatePresignedUrl(String folderName, String fileName, String contentType) {
        // Construct the full S3 key: folderName/fileName
        String key = folderName + "/" + fileName;

        // Ensure the full key does not start with a leading slash, which is incorrect in S3
        if (key.startsWith("/")) {
            key = key.substring(1);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        // Set the expiration time for the signed URL
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // URL expires after 5 minutes
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public String extractS3KeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // 1. Look for the path-style access pattern (s3.amazonaws.com/bucketName/key)
        String pathStylePrefix = ".amazonaws.com/" + bucketName + "/";
        int pathStyleIndex = fileUrl.indexOf(pathStylePrefix);
        if (pathStyleIndex != -1) {
            return fileUrl.substring(pathStyleIndex + pathStylePrefix.length());
        }

        // 2. Look for the virtual-hosted style access pattern (bucketName.s3.amazonaws.com/key)
        String virtualHostPrefix = bucketName + ".s3";
        int virtualHostIndex = fileUrl.indexOf(virtualHostPrefix);

        if (virtualHostIndex != -1) {
            // Find the start of the path after the TLD (e.g., after .com/)
            int pathStart = fileUrl.indexOf("/", virtualHostIndex + virtualHostPrefix.length());
            if (pathStart != -1 && pathStart < fileUrl.length() - 1) {
                // Return the string after the first slash of the path
                return fileUrl.substring(pathStart + 1);
            }
        }

        // If neither pattern is found, return null or the original URL (depending on strictness)
        System.err.println("Warning: Could not reliably extract S3 key from URL: " + fileUrl);
        return null;
    }

    /**
     * Deletes a file from S3 using the full S3 key/path.
     * The key must include the folder prefix (e.g., "proofs/screenshot_12345.png").
     * This method handles URL decoding and wraps the deletion in a try-catch to avoid
     * cascading failures if the file is already missing.
     * @param fullS3Key The full path to the object in S3.
     */

    public void deleteFile(String fullS3Key) {
        if (fullS3Key == null || fullS3Key.trim().isEmpty()) {
            System.out.println("S3 Delete skipped: Key is null or empty.");
            return;
        }

        String decodedKey = fullS3Key;
        try {
            // Decode URL-encoded characters (important if file names contain spaces/special chars)
            decodedKey = URLDecoder.decode(fullS3Key, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // Log if decoding fails but continue with original key
            System.err.println("Failed to decode S3 key: " + fullS3Key + ". Using original key.");
        }

        System.out.println("Attempting to delete S3 key: " + decodedKey);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(decodedKey)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("Successfully deleted S3 object: " + decodedKey);
        } catch (software.amazon.awssdk.services.s3.model.NoSuchKeyException e) {
            // This is a common and usually ignorable error: the file was already deleted.
            System.out.println("S3 object not found (already deleted?): " + decodedKey);
        } catch (Exception e) {
            // Catch other S3 exceptions (e.g., permissions, network issues)
            System.err.println("Error deleting S3 object: " + decodedKey + ". Error: " + e.getMessage());
            // You can choose to rethrow a custom exception here if the DB delete should fail too.
        }
    }
}