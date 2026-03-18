package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solo.sr4s_stats.dto.RaceDetailDto;
import solo.sr4s_stats.model.Race;
import solo.sr4s_stats.repository.RaceRepository;
import solo.sr4s_stats.repository.RaceResultRepository;
import solo.sr4s_stats.util.SlugUtils;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class RaceService {

    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;

    public RaceService(RaceRepository raceRepository, RaceResultRepository raceResultRepository) {
        this.raceRepository = raceRepository;
        this.raceResultRepository = raceResultRepository;
    }

    @Transactional(readOnly = true)
    public RaceDetailDto getRaceDetail(Long raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "RACE NOT FOUND WITH ID: " + raceId));

        var results = raceResultRepository.findResultsByRaceId(raceId).stream()
                .map(r -> new solo.sr4s_stats.dto.RaceResultDto(
                        r.finishPosition(),
                        r.gridPosition(),
                        r.carNumber(),
                        r.points(),
                        r.gapToLeader(),
                        r.dnf(),
                        r.fastestLap(),
                        new solo.sr4s_stats.dto.DriverDto(
                                r.driver().id(),
                                r.driver().displayName(),
                                r.driver().countryCode(),
                                r.driver().pictureKey(),
                                SlugUtils.toSlug(r.driver().displayName())
                        )
                ))
                .toList();

        return new RaceDetailDto(
                race.getId(),
                race.getRoundNumber(),
                race.getName(),
                race.getCircuit(),
                race.getRaceDate(),
                results
        );
    }
}
