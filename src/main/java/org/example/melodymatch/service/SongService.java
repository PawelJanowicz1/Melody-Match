package org.example.melodymatch.service;

import lombok.RequiredArgsConstructor;
import org.example.melodymatch.configs.JwtUtil;
import org.example.melodymatch.dto.SongDto;
import org.example.melodymatch.model.Song;
import org.example.melodymatch.model.User;
import org.example.melodymatch.repository.SongRepository;
import org.example.melodymatch.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public void saveSong(String token, SongDto dto) {
        String username = JwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        Song song = new Song();
        song.setTitle(dto.title());
        song.setArtist(dto.artist());
        song.setAlbum(dto.album());
        song.setUser(user);

        songRepository.save(song);
    }

    public List<Song> getSongs(String token) {
        String username = JwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();
        return songRepository.findByUserId(user.getId());
    }

    public Long getUserIdFromToken(String token) {
        String username = JwtUtil.getUsernameFromToken(token);
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    public void deleteSongById(Long id, String token) {
        String username;
        try {
            username = JwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Optional<Song> optionalSong = songRepository.findById(id);
        if (optionalSong.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found");
        }

        User user = optionalUser.get();
        Song song = optionalSong.get();

        if (!song.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this song");
        }

        songRepository.delete(song);
    }
}