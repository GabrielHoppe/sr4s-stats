package solo.sr4s_stats.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateSeasonRequest (
        @NotBlank(message = "SPREADSHEET ID IS REQUIRED")
        @Size(max = 255, message = "SPREADSHEET ID MUST BE LESS THAN 255 CHARACTERS")
        String spreadsheetId,

        @NotNull(message = "ACTIVE IS REQUIRED")
        Boolean active,

        @NotNull(message = "YEAR IS REQUIERED")
        @Min(value = 1, message = "YEAR MUST BE GREATER THAN 0")
        Integer year,

        @NotNull(message = "SUB YEAR SEASON IS REQUIRED")
        @Min(value = 1, message = "SUB YEAR SEASON NUMBER MUST BE GREATER THAN 0")
        Integer subYearSeason,

        @Size(max = 255, message = "NAME MUST BE LESS THAN 255 CHARACTERS")
        String name
) {}
