package solo.sr4s_stats.dto;

public record DriverDto(
        Long id,
        String displayName,
        String countryCode,
        String pictureKey,
        String slug
) {}
