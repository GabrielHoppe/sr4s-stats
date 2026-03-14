package solo.sr4s_stats.dto;

import java.util.List;

public record SeasonDetailDto(
        Long id,
        String name,
        int year,
        int subYearSeason,
        int dropRounds,
        boolean active,
        long totalRaces,
        List<SeasonRaceDto> races
) {
}