package solo.sr4s_stats.service.importing;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.sr4s_stats.model.Race;
import solo.sr4s_stats.model.Season;
import solo.sr4s_stats.repository.RaceRepository;
import solo.sr4s_stats.service.GoogleSheetsService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SeasonSyncService {

    private final GoogleSheetsService googleSheetsService;
    private final SheetParser sheetParser;
    private final ImportRaceService importRaceService;
    private final RaceRepository raceRepository;

    public SeasonSyncService(
            GoogleSheetsService googleSheetsService,
            SheetParser sheetParser,
            ImportRaceService importRaceService,
            RaceRepository raceRepository
    ) {
        this.googleSheetsService = googleSheetsService;
        this.sheetParser = sheetParser;
        this.importRaceService = importRaceService;
        this.raceRepository = raceRepository;
    }

    @Transactional
    public void syncSeason(Season season) {
        List<String> sheetTitles = googleSheetsService.listSheetTitles(season.getSpreadsheetId());
        Set<Integer> importedRounds = new HashSet<>();

        for (String sheetTitle : sheetTitles) {
            String firstCell = googleSheetsService.readFirstCell(season.getSpreadsheetId(), sheetTitle);

            if (!isRaceSheet(firstCell)) {
                continue;
            }

            try {
                List<List<String>> values = googleSheetsService.readSheetAsStrings(season.getSpreadsheetId(), sheetTitle);
                var parsedSheet = sheetParser.parse(values);

                importRaceService.importParsedSheet(season, parsedSheet);
                importedRounds.add(parsedSheet.roundNumber());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        deleteMissingRaces(season, importedRounds);
    }

    private boolean isRaceSheet(String firstCell) {
        if (firstCell == null) {
            return false;
        }
        return firstCell.trim().matches("(?i)^ROUND\\s+\\d+.*$");
    }

    private void deleteMissingRaces(Season season, Set<Integer> importedRounds) {
        List<Race> existingRaces = raceRepository.findAllBySeasonId(season.getId());

        for (Race race : existingRaces) {
            if (!importedRounds.contains(race.getRoundNumber())) {
                raceRepository.delete(race);
            }
        }
    }
}