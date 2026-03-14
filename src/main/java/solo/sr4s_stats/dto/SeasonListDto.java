package solo.sr4s_stats.dto;

public record SeasonListDto(
        Long id,
        String name,
        int year,
        int subYearSeason,
        int dropRounds,
        boolean active,
        long totalRaces
){}
