package com.usermanagement.user.application.controller;

import com.usermanagement.user.domain.entity.ArtistEntity;
import com.usermanagement.user.external.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artists")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;

    public ArtistController(ArtistRepository artistRepository){
        this.artistRepository=artistRepository;
    }

    @GetMapping
    public ResponseEntity<ArtistEntity> getArtist(@RequestParam("profileId") Long profileId) {
        ArtistEntity artist = artistRepository.findByProfileProfileId(profileId);
        if (artist != null) {
            return ResponseEntity.ok(artist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ArtistEntity> updateArtist(@RequestParam("profileId") Long profileId, @ModelAttribute ArtistEntity updatedArtist) {
        ArtistEntity existingArtist = artistRepository.findByProfileProfileId(profileId);
        if (existingArtist != null) {
            // Update fields of the existing artist
            existingArtist.setArtistName(updatedArtist.getArtistName());
            existingArtist.setGenre(updatedArtist.getGenre());
            existingArtist.setArtistBio(updatedArtist.getArtistBio());
            // Add other fields as necessary

            artistRepository.save(existingArtist);
            return ResponseEntity.ok(existingArtist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
