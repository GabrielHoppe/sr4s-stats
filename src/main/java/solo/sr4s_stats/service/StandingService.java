package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import solo.sr4s_stats.dto.StandingDto;
import solo.sr4s_stats.model.Driver;
import solo.sr4s_stats.model.DriverIdentity;
import solo.sr4s_stats.model.RaceResult;
import solo.sr4s_stats.model.Season;

import solo.sr4s_stats.repository.DriverIdentityRepository;
import solo.sr4s_stats.repository.RaceRepository;
import solo.sr4s_stats.repository.RaceResultRepository;
import solo.sr4s_stats.repository.SeasonRepository;
import solo.sr4s_stats.util.SlugUtils;

import java.util.*;

@Service
public class StandingService {

    private final RaceResultRepository raceResultRepository;
    private final SeasonRepository seasonRepository;
    private final DriverIdentityRepository driverIdentityRepository;
    private final RaceRepository raceRepository;

    public StandingService(
            RaceResultRepository raceResultRepository,
            SeasonRepository seasonRepository,
            DriverIdentityRepository driverIdentityRepository,
            RaceRepository raceRepository
    ) {
        this.raceResultRepository = raceResultRepository;
        this.seasonRepository = seasonRepository;
        this.driverIdentityRepository = driverIdentityRepository;
        this.raceRepository = raceRepository;
    }

    @Transactional(readOnly = true)
    public List<StandingDto> getSeasonStandings(Long seasonId) {

        Season season = seasonRepository.findById(seasonId)
                .orElseThrow();

        int dropRounds = season.getDropRounds();

        long rounds = raceRepository.countBySeasonId(seasonId);

        if (season.isActive() && rounds < 3) {
            dropRounds = 0;
        }

        List<RaceResult> results =
                raceResultRepository.findResultsBySeason(seasonId);

        Map<Driver, List<Integer>> driverPoints = new LinkedHashMap<>();

        Map<Driver, Map<Integer, Integer>> driverPositions = new HashMap<>();

        for (RaceResult rr : results) {

            Driver driver = rr.getDriver();

            driverPoints
                    .computeIfAbsent(driver,
                            d -> new ArrayList<>(Collections.nCopies((int) rounds, 0)));

            int roundIndex = rr.getRace().getRoundNumber() - 1;

            driverPoints
                    .get(driver)
                    .set(roundIndex, rr.getPoints());

            driverPositions
                    .computeIfAbsent(driver, d -> new HashMap<>())
                    .merge(rr.getFinishPosition(), 1, Integer::sum);
        }

        List<StandingDto> standings = new ArrayList<>();

        for (Map.Entry<Driver, List<Integer>> entry : driverPoints.entrySet()) {

            Driver driver = entry.getKey();
            List<Integer> points = entry.getValue();

            points.sort(Integer::compareTo);

            int total = points.stream().mapToInt(Integer::intValue).sum();

            for (int i = 0; i < dropRounds && i < points.size(); i++) {
                total -= points.get(i);
            }

            String displayName = driver.getDisplayName();

            if (displayName == null) {

                Optional<DriverIdentity> identity =
                        driverIdentityRepository.findPrimaryByDriverId(driver.getId());

                displayName = identity
                        .map(DriverIdentity::getName)
                        .orElse(null);
            }

            standings.add(new StandingDto(
                    driver.getId(),
                    displayName,
                    SlugUtils.toSlug(displayName),
                    driver.getCountryCode(),
                    driver.getPictureKey(),
                    driver.getNumber(),
                    total
            ));
        }

        standings.sort((a, b) -> {

            int cmp = Integer.compare(b.points(), a.points());
            if (cmp != 0) return cmp;

            Driver driverA = null;
            Driver driverB = null;

            for (Driver d : driverPoints.keySet()) {
                if (d.getId().equals(a.driverId())) driverA = d;
                if (d.getId().equals(b.driverId())) driverB = d;
            }

            Map<Integer, Integer> posA =
                    driverPositions.getOrDefault(driverA, Map.of());

            Map<Integer, Integer> posB =
                    driverPositions.getOrDefault(driverB, Map.of());

            int maxPosition = 50;

            for (int pos = 1; pos <= maxPosition; pos++) {

                int countA = posA.getOrDefault(pos, 0);
                int countB = posB.getOrDefault(pos, 0);

                if (countA != countB) {
                    return Integer.compare(countB, countA);
                }
            }

            return 0;
        });

        return standings;
    }
}