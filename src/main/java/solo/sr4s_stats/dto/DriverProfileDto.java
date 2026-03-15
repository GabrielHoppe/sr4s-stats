package solo.sr4s_stats.dto;

public record DriverProfileDto(
        Long id,
        String displayName,
        String slug,
        String countryCode,
        String pictureKey,
        Integer number,
        int careerRaces,
        int careerPoints,
        int podiums,
        int championships,
        PositionStatDto highestFinish,
        PositionStatDto highestGrid
) {}
