package solo.sr4s_stats.dto;

public record RaceTopThreeRowDto(
        Long raceId,
        int finishPosition,
        int carNumber,
        int points,
        boolean fastestLap,
        Long driverId,
        String driverDisplayName,
        String driverCountryCode,
        String driverPictureKey
) {
}