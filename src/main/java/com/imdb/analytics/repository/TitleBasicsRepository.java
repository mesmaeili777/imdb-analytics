package com.imdb.analytics.repository;

import com.imdb.analytics.entity.TitleBasics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleBasicsRepository extends JpaRepository<TitleBasics, String> {

    /*@Query(value = """
        SELECT
            b.tconst,
            b.primary_title,
            b.start_year,
            n.primary_name,
            n.birth_year
        FROM title_basics b
        INNER JOIN title_directors d ON b.tconst = d.tconst
        INNER JOIN title_writers w ON b.tconst = w.tconst AND d.nconst = w.nconst
        INNER JOIN name_basics n ON d.nconst = n.nconst
        WHERE n.death_year IS NULL
          AND b.title_type IN ('movie', 'tvSeries', 'tvMovie')
        ORDER BY b.start_year DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT b.tconst)
        FROM title_basics b
        INNER JOIN title_directors d ON b.tconst = d.tconst
        INNER JOIN title_writers w ON b.tconst = w.tconst AND d.nconst = w.nconst
        INNER JOIN name_basics n ON d.nconst = n.nconst
        WHERE n.death_year IS NULL
          AND b.title_type IN ('movie', 'tvSeries', 'tvMovie')
        """,
            nativeQuery = true)
    Page<Object[]> findTitlesWhereDirectorEqualsWriterAndAlive(Pageable pageable);*/

    @Query(value = """
            SELECT
                b.tconst,
                b.primary_title,
                b.start_year,
                n.primary_name,
                n.birth_year
            FROM mv_director_writer_same mv
            JOIN title_basics b
                ON b.tconst = mv.tconst
            JOIN name_basics n
                ON n.nconst = mv.nconst
            WHERE n.death_year IS NULL
              AND b.title_type IN ('movie', 'tvSeries', 'tvMovie')
            ORDER BY b.start_year DESC
            """, countQuery = """
            SELECT COUNT(*)
            FROM mv_director_writer_same mv
            JOIN title_basics b
                ON b.tconst = mv.tconst
            JOIN name_basics n
                ON n.nconst = mv.nconst
            WHERE n.death_year IS NULL
              AND b.title_type IN ('movie', 'tvSeries', 'tvMovie')
            """, nativeQuery = true)
    Page<Object[]> findTitlesWhereDirectorEqualsWriterAndAlive(Pageable pageable);

    @Query(value = """
        WITH ranked_titles AS (
            SELECT 
                b.start_year as year,
                b.tconst,
                b.primary_title,
                r.average_rating,
                r.num_votes,
                STRING_AGG(DISTINCT g.genre, ', ') as genres,
                ROW_NUMBER() OVER (
                    PARTITION BY b.start_year 
                    ORDER BY r.num_votes DESC, r.average_rating DESC
                ) as rank
            FROM title_basics b
            INNER JOIN title_genres g ON b.tconst = g.tconst
            INNER JOIN title_ratings r ON b.tconst = r.tconst
            WHERE g.genre = :genre
              AND b.title_type IN ('movie', 'tvMovie', 'video')
              AND b.start_year IS NOT NULL
              AND r.num_votes >= 1000
            GROUP BY b.start_year, b.tconst, b.primary_title, r.average_rating, r.num_votes
        )
        SELECT year, tconst, primary_title, average_rating, num_votes, genres
        FROM ranked_titles
        WHERE rank = 1
        """,
            countQuery = """
        SELECT COUNT(DISTINCT b.start_year)
        FROM title_basics b
        INNER JOIN title_genres g ON b.tconst = g.tconst
        INNER JOIN title_ratings r ON b.tconst = r.tconst
        WHERE g.genre = :genre
          AND b.title_type IN ('movie', 'tvMovie', 'video')
          AND b.start_year IS NOT NULL
          AND r.num_votes >= 1000
        """,
            nativeQuery = true)
    Page<Object[]> findBestTitlePerYearByGenre(@Param("genre") String genre, Pageable pageable);

    @Query(value = """
        SELECT 
            b.tconst,
            b.primary_title,
            b.start_year,
            p1.characters as actor1_character,
            p2.characters as actor2_character
        FROM title_principals p1
        INNER JOIN title_principals p2 ON p1.tconst = p2.tconst
        INNER JOIN title_basics b ON p1.tconst = b.tconst
        WHERE p1.nconst = :actor1Id
          AND p2.nconst = :actor2Id
          AND p1.category = 'actor'
          AND p2.category = 'actor'
        ORDER BY b.start_year DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT p1.tconst)
        FROM title_principals p1
        INNER JOIN title_principals p2 ON p1.tconst = p2.tconst
        WHERE p1.nconst = :actor1Id
          AND p2.nconst = :actor2Id
          AND p1.category = 'actor'
          AND p2.category = 'actor'
        """,
            nativeQuery = true)
    Page<Object[]> findCommonTitlesForActors(
            @Param("actor1Id") String actor1Id,
            @Param("actor2Id") String actor2Id,
            Pageable pageable);
}