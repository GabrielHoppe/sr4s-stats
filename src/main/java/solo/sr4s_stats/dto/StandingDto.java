package solo.sr4s_stats.dto;

public record StandingDto(
        Long driverId,
        String displayName,
        String countryCode,
        String pictureKey,
        Integer number,
        Integer points
) {}
