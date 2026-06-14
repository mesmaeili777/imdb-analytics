package com.imdb.analytics.controller;

import com.imdb.analytics.service.RequestCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private RequestCounterService counterService;

    @GetMapping("/requests")
    public ResponseEntity<Map<String, Long>> getRequestCount() {
        long count = counterService.getCount();
        return ResponseEntity.ok(Map.of("total_requests_since_startup", count));
    }

    @PostMapping("/requests/reset")
    public ResponseEntity<Map<String, String>> resetRequestCount() {
        counterService.reset();
        return ResponseEntity.ok(Map.of("message", "Request counter reset successfully"));
    }
}