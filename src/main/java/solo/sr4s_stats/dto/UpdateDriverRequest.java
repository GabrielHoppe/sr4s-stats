package solo.sr4s_stats.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateDriverRequest (
    String displayName,

    @Pattern(
            regexp = "^[A-Za-z]{3}$",
            message = "COUNTRY CODE MUST BE 3 LETTERS CODE"
    )
    String countryCode,

    @Size(
            max = 255,
            message = "MAX LENGTH OF PICTURE KEY IS 255"
    )
    String pictureKey
) {}
