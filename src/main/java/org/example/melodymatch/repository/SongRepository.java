package org.example.melodymatch.repository;

import org.example.melodymatch.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByUserId(Long userId);
}