package com.imdb.analytics.service;

import com.imdb.analytics.dto.*;
import com.imdb.analytics.repository.TitleBasicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TitleQueryService {

    @Autowired
    private TitleBasicsRepository titleBasicsRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page<TitleDirectorWriterDTO> getTitlesWithDirectorEqualsWriterAlive(Pageable pageable) {
        Page<Object[]> resultPage = titleBasicsRepository.findTitlesWhereDirectorEqualsWriterAndAlive(pageable);

        return resultPage.map(row -> new TitleDirectorWriterDTO(
                (String) row[0],  //tconst
                (String) row[1],  //primaryTitle
                (Integer) row[2], //startYear
                (String) row[3],  //directorName
                (Integer) row[4]  //directorBirthYear
        ));
    }

    public Page<BestTitlePerYearDTO> getBestTitlesPerYearByGenre(String genre, Pageable pageable) {
        Page<Object[]> resultPage = titleBasicsRepository.findBestTitlePerYearByGenre(genre, pageable);

        return resultPage.map(row -> new BestTitlePerYearDTO(
                (Integer) row[0], //year
                (String) row[1],  //tconst
                (String) row[2],  //primaryTitle
                (Double) row[3],  //averageRating
                (Integer) row[4], //numVotes
                (String) row[5]   //genres
        ));
    }

    public Page<CommonTitleForActorsDTO> getCommonTitlesForActors(String actor1Name, String actor2Name, Pageable pageable) {
        String findActorSql = "SELECT nconst, primary_name FROM name_basics WHERE primary_name ILIKE ? LIMIT 1";

        List<Map<String, Object>> actor1Results = jdbcTemplate.queryForList(findActorSql, actor1Name);
        List<Map<String, Object>> actor2Results = jdbcTemplate.queryForList(findActorSql, actor2Name);

        if (actor1Results.isEmpty() || actor2Results.isEmpty()) {
            throw new RuntimeException("Actor(s) not found: " +
                    (actor1Results.isEmpty() ? actor1Name : "") +
                    (actor2Results.isEmpty() ? " " + actor2Name : ""));
        }

        String actor1Id = (String) actor1Results.get(0).get("nconst");
        String actor2Id = (String) actor2Results.get(0).get("nconst");
        String actor1FullName = (String) actor1Results.get(0).get("primary_name");
        String actor2FullName = (String) actor2Results.get(0).get("primary_name");

        Page<Object[]> resultPage = titleBasicsRepository.findCommonTitlesForActors(actor1Id, actor2Id, pageable);

        return resultPage.map(row -> new CommonTitleForActorsDTO(
                (String) row[0],  //tconst
                (String) row[1],  //primaryTitle
                (Integer) row[2], //startYear
                actor1FullName,   //actor1Name
                actor2FullName,   //actor2Name
                (String) row[3],  //actor1Character
                (String) row[4]   //actor2Character
        ));
    }
}