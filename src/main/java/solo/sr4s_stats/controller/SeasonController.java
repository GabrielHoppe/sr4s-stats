package solo.sr4s_stats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solo.sr4s_stats.dto.SeasonDetailDto;
import solo.sr4s_stats.dto.SeasonListDto;
import solo.sr4s_stats.service.SeasonService;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

        private final SeasonService seasonService;

        public SeasonController(SeasonService seasonService) {
            this.seasonService = seasonService;
        }

        @GetMapping
        public List<SeasonListDto> listSeasons() {
            return seasonService.listSeasons();
        }

        @GetMapping("/{seasonId}/races")
        public SeasonDetailDto getSeasonRaces(@PathVariable Long seasonId) {
            return seasonService.getSeasonDetail(seasonId);
        }
}
