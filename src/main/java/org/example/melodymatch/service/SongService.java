package org.example.melodymatch.service;

import lombok.RequiredArgsConstructor;
import org.example.melodymatch.configs.JwtUtil;
import org.example.melodymatch.dto.SongDto;
import org.example.melodymatch.model.Song;
import org.example.melodymatch.model.User;
import org.example.melodymatch.repository.SongRepository;
import org.example.melodymatch.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}