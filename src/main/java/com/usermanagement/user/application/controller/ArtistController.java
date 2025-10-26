package com.usermanagement.user.application.controller;

import com.usermanagement.user.application.dto.ArtistSearchDTO;
import com.usermanagement.user.application.dto.ProfileDTO;
import com.usermanagement.user.domain.entity.ArtistEntity;
import com.usermanagement.user.domain.service.ArtistService;
import com.usermanagement.user.external.repository.ArtistRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {
    @Autowired
    ArtistService artistService;


    @GetMapping
    public ResponseEntity<ArtistEntity> getArtist(@RequestParam(value="profileId", required = false) Long profileId , @RequestParam(value="artistId", required = false) Long artistId) {
        if(artistId != null){
            return artistService.getArtistById(artistId);
        }if(profileId != null){
            return artistService.getArtistByProfileId(profileId);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ArtistEntity> updateArtist(@RequestParam("profileId") Long profileId, @ModelAttribute ArtistEntity updatedArtist, @RequestParam(value = "artistImage", required = false) MultipartFile artistImage) {
        ArtistEntity artist = artistService.updateArtist(profileId, updatedArtist, artistImage);
        return ResponseEntity.ok(artist);
    }

    @GetMapping("/search")
    public List<ArtistSearchDTO> getArtistSearch(@RequestParam ("query") String query) {
        return artistService.searchArtists(query);

    }

}
