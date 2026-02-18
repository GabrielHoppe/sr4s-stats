package solo.sr4s_stats.service.importing.model;

public record ParsedRow(
        int finishPos,
        int carNumber,
        String driverName,
        String gapToLeader,
        String fastestTimeRaw,
        int startPos,
        String dnfStatus,
        Integer iracingId,
        int points
) {}
