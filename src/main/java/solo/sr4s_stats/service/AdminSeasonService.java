package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import solo.sr4s_stats.dto.StandingDto;
import solo.sr4s_stats.model.Driver;
import solo.sr4s_stats.model.Season;
import solo.sr4s_stats.repository.DriverRepository;
import solo.sr4s_stats.repository.SeasonRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AdminSeasonService {
    private final SeasonRepository seasonRepository;
    private final StandingService standingService;
    private final DriverRepository driverRepository;

    public AdminSeasonService(
            SeasonRepository seasonRepository,
            StandingService standingService,
            DriverRepository driverRepository
    ) {
        this.seasonRepository = seasonRepository;
        this.standingService = standingService;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public Season createSeason(
            String spreadsheetId,
            Boolean active,
            Integer year,
            Integer subYearSeason,
            Integer dropRounds,
            String name
    ) {
        if (spreadsheetId == null || !StringUtils.hasText(spreadsheetId.trim())) {
            throw new ResponseStatusException(BAD_REQUEST, "SPREADSHEET ID IS REQUIRED");
        }

        if (active == null) {
            throw new ResponseStatusException(BAD_REQUEST, "ACTIVE IS REQUIRED");
        }

        if (year == null) {
            throw new ResponseStatusException(BAD_REQUEST, "YEAR IS REQUIRED");
        }

        if (subYearSeason == null) {
            throw new ResponseStatusException(BAD_REQUEST, "SUB YEAR SEASON IS REQUIRED");
        }

        if (year < 1) {
            throw new ResponseStatusException(BAD_REQUEST, "YEAR MUST BE GREATER THAN 0");
        }

        if (subYearSeason < 1) {
            throw new ResponseStatusException(BAD_REQUEST, "SUB YEAR SEASON MUST BE GREATER THAN 0");
        }

        if (dropRounds < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "DROP ROUNDS MUST BE EQUAL TO 0 OR GREATER");
        }

        if (seasonRepository.existsByYearAndSubYearSeason(year, subYearSeason)) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "A SEASON ALREADY EXISTS WITH THE GIVEN YEAR AND SUB YEAR SEASON"
            );
        }
        Season season = new Season();
        season.setSpreadsheetId(spreadsheetId.trim());
        season.setActive(active);
        season.setYear(year);
        season.setSubYearSeason(subYearSeason);
        season.setDropRounds(dropRounds);

        if (name == null || !StringUtils.hasText(name.trim())) {
            season.setName(null);
        } else {
            season.setName(name.trim());
        }

        return seasonRepository.save(season);
    }

    @Transactional
    public void deleteSeason(Long seasonId) {
        if (seasonId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "SEASON ID IS REQUIRED");
        }

        if (!seasonRepository.existsById(seasonId)) {
            throw new ResponseStatusException(BAD_REQUEST, "SEASON NOT FOUND WITH ID: " + seasonId);
        }

        seasonRepository.deleteById(seasonId);
    }

    @Transactional
    public void updateSeason(Long seasonId, Boolean active, Integer dropRounds, String name) {
        if (seasonId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "SEASON ID IS REQUIRED");
        }

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "SEASON NOT FOUND WITH ID: " + seasonId));

        if (active != null) {
            season.setActive(active);
            if (!active) {
                List<StandingDto> standings = standingService.getSeasonStandings(seasonId);
                if (!standings.isEmpty()) {
                    Driver champion = driverRepository.getReferenceById(standings.get(0).driverId());
                    season.setChampionDriver(champion);
                }
            } else {
                season.setChampionDriver(null);
            }
        }

        if (dropRounds != null) {
            if (dropRounds < 0) {
                throw new ResponseStatusException(BAD_REQUEST, "DROP ROUNDS MUST BE 0 OR GREATER");
            }
            season.setDropRounds(dropRounds);
        }

        if (name != null) {
            String normalized = name.trim();
            if (!StringUtils.hasText(normalized)) {
                season.setName(null);
            } else {
                season.setName(normalized);
            }
        }

        seasonRepository.save(season);
    }
}
