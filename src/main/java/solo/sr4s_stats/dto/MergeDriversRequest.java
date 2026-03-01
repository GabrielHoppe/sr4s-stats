package solo.sr4s_stats.dto;

import jakarta.validation.constraints.NotNull;

public record MergeDriversRequest(
        @NotNull Long winnerDriverId,
        @NotNull Long loserDriverId
) {}
