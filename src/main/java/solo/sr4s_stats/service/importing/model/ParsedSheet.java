package solo.sr4s_stats.service.importing.model;

import java.time.LocalDate;
import java.util.List;

public record ParsedSheet(
        String roundTitle,
        int roundNumber,
        String circuit,
        String championship,
        LocalDate raceDate,
        List<ParsedRow> rows
) {}
