package solo.sr4s_stats.dto;

import jakarta.validation.constraints.Size;

public record UpdateSeasonRequest(
        Boolean active,
        @Size(max = 255, message = "NAME MUST BE LESS THAN 255 CHARS")
        String name
) {}
