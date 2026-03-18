package solo.sr4s_stats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solo.sr4s_stats.dto.RaceDetailDto;
import solo.sr4s_stats.service.RaceService;

@RestController
@RequestMapping("/api/races")
public class RaceController {

    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping("/{raceId}")
    public RaceDetailDto getRaceDetail(@PathVariable Long raceId) {
        return raceService.getRaceDetail(raceId);
    }
}
