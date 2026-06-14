package com.imdb.analytics.controller;

import com.imdb.analytics.service.DatasetImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private DatasetImportService importService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/dataset")
    public ResponseEntity<String> importDataset() {
        try {
            long startTime = System.currentTimeMillis();
            importService.importAll();
            long duration = (System.currentTimeMillis() - startTime) / 1000;
            return ResponseEntity.ok("Dataset import completed successfully in " + duration + " seconds");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Import failed: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getImportStats() {
        try {
            long nameCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM name_basics", Long.class);
            long titleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_basics", Long.class);
            long ratingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_ratings", Long.class);
            long principalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_principals", Long.class);
            long directorCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_directors", Long.class);
            long writerCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_writers", Long.class);
            long genreCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_genres", Long.class);

            return ResponseEntity.ok(Map.of(
                    "name_basics", nameCount,
                    "title_basics", titleCount,
                    "title_ratings", ratingCount,
                    "title_principals", principalCount,
                    "title_directors", directorCount,
                    "title_writers", writerCount,
                    "title_genres", genreCount
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}