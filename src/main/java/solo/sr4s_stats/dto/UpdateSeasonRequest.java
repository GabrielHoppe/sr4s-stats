package solo.sr4s_stats.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateSeasonRequest(
        Boolean active,

        @Min(value = 0, message = "DROP ROUNDS MUST BE 0 OR GREATER")
        Integer dropRounds,

        @Size(max = 255, message = "NAME MUST BE LESS THAN 255 CHARS")
        String name
) {}
