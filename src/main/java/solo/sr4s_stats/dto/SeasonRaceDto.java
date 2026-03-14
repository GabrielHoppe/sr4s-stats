package solo.sr4s_stats.dto;

import java.time.LocalDate;
import java.util.List;

public record SeasonRaceDto(
        Long id,
        int roundNumber,
        String name,
        String circuit,
        LocalDate raceDate,
        List<RaceTopThreeResultDto> topThree
) {
}