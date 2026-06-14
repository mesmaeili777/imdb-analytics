package com.imdb.analytics.controller;

import com.imdb.analytics.dto.*;
import com.imdb.analytics.service.TitleQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

    @Autowired
    private TitleQueryService titleQueryService;

    @GetMapping("/director-equals-writer/alive")
    public ResponseEntity<Page<TitleDirectorWriterDTO>> getTitlesWhereDirectorEqualsWriterAndAlive(@PageableDefault(size = 20) Pageable pageable) {
        Page<TitleDirectorWriterDTO> result = titleQueryService.getTitlesWithDirectorEqualsWriterAlive(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/best-per-year")
    public ResponseEntity<Page<BestTitlePerYearDTO>> getBestTitlesPerYearByGenre(@RequestParam String genre, @PageableDefault(size = 20) Pageable pageable) {
        Page<BestTitlePerYearDTO> result = titleQueryService.getBestTitlesPerYearByGenre(genre, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/common-titles")
    public ResponseEntity<Page<CommonTitleForActorsDTO>> getCommonTitlesForActors(@RequestParam String actor1, @RequestParam String actor2, @PageableDefault(size = 20) Pageable pageable) {
        Page<CommonTitleForActorsDTO> result = titleQueryService.getCommonTitlesForActors(actor1, actor2, pageable);
        return ResponseEntity.ok(result);
    }
}