package solo.sr4s_stats.dto;

public record RaceResultDto(
        int finishPosition,
        int gridPosition,
        int carNumber,
        int points,
        String gapToLeader,
        boolean dnf,
        boolean fastestLap,
        DriverDto driver
) {}
