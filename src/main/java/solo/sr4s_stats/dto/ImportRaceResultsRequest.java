package solo.sr4s_stats.dto;

import java.util.List;

public record ImportRaceResultsRequest(
        List<List<String>> values
) {}
