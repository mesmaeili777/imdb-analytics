package com.imdb.analytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetImportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int BATCH_SIZE = 5000;

    @Transactional
    public void importAll() throws Exception {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting optimized import process...");

        try {
            disableConstraintsAndTriggers();

            importNameBasics();
            importTitleBasics();
            importTitleRatings();
            importTitlePrincipals();
            importTitleCrewNormalized();

            System.out.println("All data imported successfully!");

        } catch (Exception e) {
            System.err.println("Import failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            enableConstraintsAndTriggers();
            createIndexesAfterImport();
            createMaterializedView();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total import time: " + (endTime - startTime) / 1000 + " seconds");
    }

    private void importNameBasics() throws Exception {
        System.out.println("Importing name.basics.tsv...");
        long start = System.currentTimeMillis();

        Path path = Paths.get("data/name.basics.tsv");
        String sql = "INSERT INTO name_basics (nconst, primary_name, birth_year, death_year, primary_profession) VALUES (?, ?, ?, ?, ?)";

        List<Object[]> batch = new ArrayList<>();
        int totalRows = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\t", -1);
                if (cols.length < 5) continue;

                Object[] params = new Object[5];
                params[0] = cols[0];
                params[1] = cols[1].length() > 500 ? cols[1].substring(0, 500) : cols[1];
                params[2] = "\\N".equals(cols[2]) ? null : Integer.parseInt(cols[2]);
                params[3] = "\\N".equals(cols[3]) ? null : Integer.parseInt(cols[3]);
                params[4] = "\\N".equals(cols[4]) ? null : (cols[4].length() > 500 ? cols[4].substring(0, 500) : cols[4]);

                batch.add(params);
                totalRows++;

                if (batch.size() >= BATCH_SIZE) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    batch.clear();
                    if (totalRows % 100000 == 0) {
                        System.out.println("Progress: " + String.format("%,d", totalRows) + " NameBasics committed...");
                    }
                }
            }
            if (!batch.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batch);
            }
        }

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM name_basics", Integer.class);
        System.out.println("  Imported " + String.format("%,d", totalRows) + " rows into name_basics (total in DB: " + String.format("%,d", count) + ") in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    private void importTitleBasics() throws Exception {
        System.out.println("Importing title.basics.tsv...");
        long start = System.currentTimeMillis();

        Path path = Paths.get("data/title.basics.tsv");
        String titleSql = "INSERT INTO title_basics (tconst, title_type, primary_title, original_title, is_adult, start_year, end_year, runtime_minutes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String genreSql = "INSERT INTO title_genres (tconst, genre) VALUES (?, ?)";

        List<Object[]> titleBatch = new ArrayList<>();
        List<Object[]> genreBatch = new ArrayList<>();
        int totalTitles = 0;
        int totalGenres = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\t", -1);
                if (cols.length < 9) continue;

                Object[] titleParams = new Object[8];
                titleParams[0] = cols[0];
                titleParams[1] = cols[1];
                titleParams[2] = cols[2].length() > 500 ? cols[2].substring(0, 500) : cols[2];
                titleParams[3] = cols[3].length() > 500 ? cols[3].substring(0, 500) : cols[3];
                titleParams[4] = "1".equals(cols[4]); // Convert to boolean
                titleParams[5] = "\\N".equals(cols[5]) ? null : Integer.parseInt(cols[5]);
                titleParams[6] = "\\N".equals(cols[6]) ? null : Integer.parseInt(cols[6]);
                titleParams[7] = "\\N".equals(cols[7]) ? null : Integer.parseInt(cols[7]);

                titleBatch.add(titleParams);
                totalTitles++;

                if (!"\\N".equals(cols[8])) {
                    String[] genres = cols[8].split(",");
                    for (String genre : genres) {
                        if (genre != null && !genre.trim().isEmpty()) {
                            genreBatch.add(new Object[]{cols[0], genre.trim()});
                            totalGenres++;

                            if (genreBatch.size() >= BATCH_SIZE) {
                                jdbcTemplate.batchUpdate(genreSql, genreBatch);
                                genreBatch.clear();
                            }
                        }
                    }
                }

                if (titleBatch.size() >= BATCH_SIZE) {
                    jdbcTemplate.batchUpdate(titleSql, titleBatch);
                    titleBatch.clear();
                    if (totalTitles % 100000 == 0) {
                        System.out.println("Progress: " + String.format("%,d", totalTitles) + " TitleBasics committed...");
                    }
                }
            }

            if (!titleBatch.isEmpty()) {
                jdbcTemplate.batchUpdate(titleSql, titleBatch);
            }
            if (!genreBatch.isEmpty()) {
                jdbcTemplate.batchUpdate(genreSql, genreBatch);
            }
        }

        Integer titleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_basics", Integer.class);
        Integer genreCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_genres", Integer.class);
        System.out.println("  Imported " + String.format("%,d", totalTitles) + " titles and " +
                         String.format("%,d", totalGenres) + " genres (total titles: " + String.format("%,d", titleCount) + ") in " +
                         (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    private void importTitleRatings() throws Exception {
        System.out.println("Importing title.ratings.tsv...");
        long start = System.currentTimeMillis();

        Path path = Paths.get("data/title.ratings.tsv");
        String sql = "INSERT INTO title_ratings (tconst, average_rating, num_votes) VALUES (?, ?, ?)";

        List<Object[]> batch = new ArrayList<>();
        int totalRows = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\t", -1);
                if (cols.length < 3) continue;

                Object[] params = new Object[3];
                params[0] = cols[0];
                params[1] = "\\N".equals(cols[1]) ? null : Double.parseDouble(cols[1]);
                params[2] = "\\N".equals(cols[2]) ? null : Integer.parseInt(cols[2]);

                batch.add(params);
                totalRows++;

                if (batch.size() >= BATCH_SIZE) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batch);
            }
        }

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_ratings", Integer.class);
        System.out.println("  Imported " + String.format("%,d", totalRows) + " ratings (total in DB: " + String.format("%,d", count) + ") in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    private void importTitlePrincipals() throws Exception {
        System.out.println("Importing title.principals.tsv (largest file)...");
        long start = System.currentTimeMillis();

        Path path = Paths.get("data/title.principals.tsv");
        String sql = "INSERT INTO title_principals (tconst, ordering, nconst, category, job, characters) VALUES (?, ?, ?, ?, ?, ?)";

        List<Object[]> batch = new ArrayList<>();
        int totalRows = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\t", -1);
                if (cols.length < 6) continue;

                Object[] params = new Object[6];
                params[0] = cols[0]; // tconst
                params[1] = "\\N".equals(cols[1]) ? null : Integer.parseInt(cols[1]); // ordering
                params[2] = cols[2]; // nconst
                params[3] = cols[3].length() > 100 ? cols[3].substring(0, 100) : cols[3]; // category
                params[4] = "\\N".equals(cols[4]) ? null : (cols[4].length() > 500 ? cols[4].substring(0, 500) : cols[4]); // job
                params[5] = "\\N".equals(cols[5]) ? null : (cols[5].length() > 500 ? cols[5].substring(0, 500) : cols[5]); // characters

                batch.add(params);
                totalRows++;

                if (batch.size() >= BATCH_SIZE) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    batch.clear();
                    if (totalRows % 250000 == 0) {
                        System.out.println("Progress: " + String.format("%,d", totalRows) + " TitlePrincipals committed...");
                    }
                }
            }

            if (!batch.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batch);
            }
        }

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_principals", Integer.class);
        System.out.println("  Imported " + String.format("%,d", totalRows) + " rows into title_principals (total in DB: " + String.format("%,d", count) + ") in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    private void importTitleCrewNormalized() throws Exception {
        System.out.println("Processing title.crew.tsv into normalized tables...");
        long start = System.currentTimeMillis();

        Path path = Paths.get("data/title.crew.tsv");

        jdbcTemplate.execute("DELETE FROM title_directors");
        jdbcTemplate.execute("DELETE FROM title_writers");

        List<Object[]> directorsBatch = new ArrayList<>();
        List<Object[]> writersBatch = new ArrayList<>();
        int directorsCount = 0;
        int writersCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\t", -1);
                if (cols.length < 3) continue;

                String tconst = cols[0];

                if (!"\\N".equals(cols[1]) && cols[1] != null && !cols[1].trim().isEmpty()) {
                    String[] directors = cols[1].split(",");
                    for (String nconst : directors) {
                        String trimmedNconst = nconst.trim();
                        if (!trimmedNconst.isEmpty()) {
                            directorsBatch.add(new Object[]{tconst, trimmedNconst});
                            directorsCount++;

                            if (directorsBatch.size() >= BATCH_SIZE) {
                                jdbcTemplate.batchUpdate("INSERT INTO title_directors (tconst, nconst) VALUES (?, ?)", directorsBatch);
                                directorsBatch.clear();
                            }
                        }
                    }
                }

                if (!"\\N".equals(cols[2]) && cols[2] != null && !cols[2].trim().isEmpty()) {
                    String[] writers = cols[2].split(",");
                    for (String nconst : writers) {
                        String trimmedNconst = nconst.trim();
                        if (!trimmedNconst.isEmpty()) {
                            writersBatch.add(new Object[]{tconst, trimmedNconst});
                            writersCount++;

                            if (writersBatch.size() >= BATCH_SIZE) {
                                jdbcTemplate.batchUpdate("INSERT INTO title_writers (tconst, nconst) VALUES (?, ?)", writersBatch);
                                writersBatch.clear();
                            }
                        }
                    }
                }

                lineCount++;
                if (lineCount % 100000 == 0) {
                    System.out.println("Processed " + String.format("%,d", lineCount) + " crew records...");
                }
            }

            if (!directorsBatch.isEmpty()) {
                jdbcTemplate.batchUpdate("INSERT INTO title_directors (tconst, nconst) VALUES (?, ?)", directorsBatch);
            }
            if (!writersBatch.isEmpty()) {
                jdbcTemplate.batchUpdate("INSERT INTO title_writers (tconst, nconst) VALUES (?, ?)", writersBatch);
            }
        }

        Integer dirCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_directors", Integer.class);
        Integer wriCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM title_writers", Integer.class);
        System.out.println("  Processed " + String.format("%,d", directorsCount) + " director and " +
                         String.format("%,d", writersCount) + " writer relationships (total directors: " + String.format("%,d", dirCount) +
                         ", writers: " + String.format("%,d", wriCount) + ") in " +
                         (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    private void disableConstraintsAndTriggers() {
        System.out.println("Disabling constraints and triggers...");
        try {
            jdbcTemplate.execute("SET session_replication_role = 'replica'");
            System.out.println("Foreign key constraints disabled");
        } catch (Exception e) {
            System.out.println("Could not disable constraints: " + e.getMessage());
        }
    }

    private void enableConstraintsAndTriggers() {
        System.out.println("Re-enabling constraints and triggers...");
        try {
            jdbcTemplate.execute("SET session_replication_role = 'origin'");
            System.out.println("Foreign key constraints re-enabled");
        } catch (Exception e) {
            System.out.println("Could not re-enable constraints: " + e.getMessage());
        }
    }

    private void createMaterializedView() {
        System.out.println("Creating materialized view...");
        try {
            jdbcTemplate.execute("CREATE MATERIALIZED view IF NOT EXISTS mv_director_writer_same as SELECT distinct d.tconst, d.nconst FROM title_directors d JOIN title_writers w ON d.tconst = w.tconst AND d.nconst = w.nconst");
            System.out.println("Materialized view mv_director_writer_same created");
        } catch (Exception e) {
            System.out.println("Could create materialized view: " + e.getMessage());
        }
    }

    private void createIndexesAfterImport() {
        System.out.println("Creating indexes for optimal query performance...");
        long start = System.currentTimeMillis();

        String[] indexCommands = {
                "CREATE INDEX IF NOT EXISTS idx_princ_tconst ON title_principals (tconst)",
                "CREATE INDEX IF NOT EXISTS idx_princ_nconst ON title_principals (nconst)",
                "CREATE INDEX IF NOT EXISTS idx_princ_nconst_category ON title_principals (nconst, category)",
                "CREATE INDEX IF NOT EXISTS idx_genre_tconst ON title_genres (tconst)",
                "CREATE INDEX IF NOT EXISTS idx_genre_name ON title_genres (genre)",
                "CREATE INDEX IF NOT EXISTS idx_dir_tconst ON title_directors (tconst)",
                "CREATE INDEX IF NOT EXISTS idx_dir_nconst ON title_directors (nconst)",
                "CREATE INDEX IF NOT EXISTS idx_wri_tconst ON title_writers (tconst)",
                "CREATE INDEX IF NOT EXISTS idx_wri_nconst ON title_writers (nconst)",
                "CREATE INDEX IF NOT EXISTS idx_rating_votes ON title_ratings (num_votes DESC)",
                "CREATE INDEX idx_title_type_startyear ON public.title_basics USING btree (title_type, start_year DESC)",
                "CREATE INDEX idx_title_basics_tconst_type ON public.title_basics USING btree (tconst, title_type)"
        };

        for (String command : indexCommands) {
            try {
                System.out.println("  Executing: " + command);
                jdbcTemplate.execute(command);
            } catch (Exception e) {
                System.out.println("Could not execute: " + command + " -> " + e.getMessage());
            }
        }

        try {
            jdbcTemplate.execute("ANALYZE");
        } catch (Exception e) {
            System.out.println("Could not update statistics: " + e.getMessage());
        }

        long duration = (System.currentTimeMillis() - start) / 1000;
        System.out.println("All indexes created in " + duration + " seconds!");
    }

}