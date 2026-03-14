package solo.sr4s_stats.dto;

public record RaceTopThreeDriverDto(
        Long id,
        String displayName,
        String countryCode,
        String pictureKey
) {
}