package com.usermanagement.user.domain.service;

import com.usermanagement.user.domain.entity.ArtistEntity;
import com.usermanagement.user.external.repository.ArtistRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class ArtistService {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    private S3Client s3Client;
    private final String bucketName = "sonex2";

    public ResponseEntity<ArtistEntity> getArtist(Long profileId) {
        ArtistEntity artist = artistRepository.findByProfileProfileId(profileId);
        if (artist != null) {
            return ResponseEntity.ok(artist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ArtistEntity updateArtist(Long profileId, ArtistEntity updatedArtist, MultipartFile artistImage) {
        ArtistEntity existingArtist = artistRepository.findByProfileProfileId(profileId);
        if (existingArtist == null) {
            throw new IllegalArgumentException("Artist not found");
        }
        String username = existingArtist.getProfile().getEmail();
        // Update fields
        existingArtist.setArtistName(updatedArtist.getArtistName());
        existingArtist.setInstagram(updatedArtist.getInstagram());
        existingArtist.setSpotify(updatedArtist.getSpotify());
        existingArtist.setYoutube(updatedArtist.getYoutube());
        existingArtist.setArtistBio(updatedArtist.getArtistBio());
        existingArtist.setGenre(updatedArtist.getGenre());
        existingArtist.setWebsite(updatedArtist.getWebsite());
        existingArtist.setCreatedAt(LocalDateTime.now());

        // Handle artist image upload
        if (artistImage != null && !artistImage.isEmpty()) {
            try {
                // Delete old image if exists
                if (existingArtist.getArtistImageURL() != null && !existingArtist.getArtistImageURL().isEmpty()) {
                    String existingUrl = existingArtist.getArtistImageURL();
                    String existingS3Key = existingUrl.substring(existingUrl.indexOf(".com/") + 5);
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(existingS3Key)
                            .build());
                }

                // Compress and upload new image
                String uniqueFileName = System.currentTimeMillis() + "_" + artistImage.getOriginalFilename();
                String s3Key = username + "/artist_images/" + uniqueFileName;

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(artistImage.getInputStream())
                        .size(800, 800)
                        .outputQuality(0.7)
                        .toOutputStream(outputStream);

                byte[] compressedBytes = outputStream.toByteArray();

                try (InputStream inputStream = new ByteArrayInputStream(compressedBytes)) {
                    s3Client.putObject(
                            PutObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(s3Key)
                                    .contentType(artistImage.getContentType())
                                    .build(),
                            RequestBody.fromInputStream(inputStream, compressedBytes.length)
                    );
                }

                String publicUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
                existingArtist.setArtistImageURL(publicUrl);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload artist image to S3", e);
            }
        }

        return artistRepository.save(existingArtist);
    }
}
