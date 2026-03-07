package solo.sr4s_stats.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import solo.sr4s_stats.dto.CreateSeasonRequest;
import solo.sr4s_stats.model.Season;
import solo.sr4s_stats.service.AdminSeasonService;

import java.net.URI;

@RestController
@RequestMapping("/admin")
public class AdminSeasonController {
    private final AdminSeasonService adminSeasonService;

    public AdminSeasonController(AdminSeasonService adminSeasonService) {
        this.adminSeasonService = adminSeasonService;
    }

    @PostMapping("/seasons")
    public ResponseEntity<Void> createSeason(@Valid @RequestBody CreateSeasonRequest req) {
        Season season = adminSeasonService.createSeason(
                req.spreadsheetId(),
                req.active(),
                req.year(),
                req.subYearSeason(),
                req.name()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(season.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
