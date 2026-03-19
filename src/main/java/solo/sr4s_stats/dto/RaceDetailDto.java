package solo.sr4s_stats.dto;

import java.time.LocalDate;
import java.util.List;

public record RaceDetailDto(
        Long raceId,
        Long seasonId,
        String seasonName,
        int roundNumber,
        String name,
        String circuit,
        LocalDate raceDate,
        List<RaceResultDto> results
) {}
