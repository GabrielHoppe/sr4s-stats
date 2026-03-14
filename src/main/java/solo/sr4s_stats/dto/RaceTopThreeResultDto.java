package solo.sr4s_stats.dto;

public record RaceTopThreeResultDto(
        int finishPosition,
        int carNumber,
        int points,
        boolean fastestLap,
        DriverDto driver
) {
}