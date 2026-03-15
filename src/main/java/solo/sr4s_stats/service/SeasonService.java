package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solo.sr4s_stats.dto.*;
import solo.sr4s_stats.model.Race;
import solo.sr4s_stats.model.Season;
import solo.sr4s_stats.repository.RaceRepository;
import solo.sr4s_stats.repository.RaceResultRepository;
import solo.sr4s_stats.repository.SeasonRepository;
import solo.sr4s_stats.util.SlugUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;

    public SeasonService(
            SeasonRepository seasonRepository,
            RaceRepository raceRepository,
            RaceResultRepository raceResultRepository
    ) {
        this.seasonRepository = seasonRepository;
        this.raceRepository = raceRepository;
        this.raceResultRepository = raceResultRepository;
    }

    @Transactional(readOnly = true)
    public List<SeasonListDto> listSeasons() {
        return seasonRepository.findAllSeasonSummaries();
    }

    @Transactional(readOnly = true)
    public SeasonDetailDto getSeasonDetail(Long seasonId) {

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "SEASON NOT FOUND WITH ID: " + seasonId
                ));

        List<Race> races = raceRepository.findAllBySeasonIdOrderByRoundNumberAsc(seasonId);

        List<RaceTopThreeRowDto> podiumRows =
                raceResultRepository.findTopThreeRowsBySeasonId(seasonId);

        Map<Long, List<RaceTopThreeResultDto>> podiumByRaceId = podiumRows.stream()
                .collect(Collectors.groupingBy(
                        RaceTopThreeRowDto::raceId,
                        LinkedHashMap::new,
                        Collectors.mapping(this::mapRowToResult, Collectors.toList())
                ));

        List<SeasonRaceDto> raceDtos = races.stream()
                .map(race -> new SeasonRaceDto(
                        race.getId(),
                        race.getRoundNumber(),
                        race.getName(),
                        race.getCircuit(),
                        race.getRaceDate(),
                        podiumByRaceId.getOrDefault(race.getId(), List.of())
                ))
                .toList();

        return new SeasonDetailDto(
                season.getId(),
                season.getName(),
                season.getYear(),
                season.getSubYearSeason(),
                season.getDropRounds(),
                season.isActive(),
                races.size(),
                raceDtos
        );
    }

    private RaceTopThreeResultDto mapRowToResult(RaceTopThreeRowDto row) {
        return new RaceTopThreeResultDto(
                row.finishPosition(),
                row.carNumber(),
                row.points(),
                row.fastestLap(),
                new DriverDto(
                        row.driverId(),
                        row.driverDisplayName(),
                        row.driverCountryCode(),
                        row.driverPictureKey(),
                        SlugUtils.toSlug(row.driverDisplayName())
                )
        );
    }
}
