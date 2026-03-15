package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solo.sr4s_stats.dto.DriverDto;
import solo.sr4s_stats.dto.DriverProfileDto;
import solo.sr4s_stats.dto.PositionStatDto;
import solo.sr4s_stats.model.Driver;
import solo.sr4s_stats.model.DriverIdentity;
import solo.sr4s_stats.model.RaceResult;
import solo.sr4s_stats.repository.DriverIdentityRepository;
import solo.sr4s_stats.repository.DriverRepository;
import solo.sr4s_stats.repository.RaceResultRepository;
import solo.sr4s_stats.repository.SeasonRepository;
import solo.sr4s_stats.util.SlugUtils;

import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverIdentityRepository driverIdentityRepository;
    private final RaceResultRepository raceResultRepository;
    private final SeasonRepository seasonRepository;

    public DriverService(
            DriverRepository driverRepository,
            DriverIdentityRepository driverIdentityRepository,
            RaceResultRepository raceResultRepository,
            SeasonRepository seasonRepository
    ) {
        this.driverRepository = driverRepository;
        this.driverIdentityRepository = driverIdentityRepository;
        this.raceResultRepository = raceResultRepository;
        this.seasonRepository = seasonRepository;
    }

    @Transactional(readOnly = true)
    public List<DriverDto> getDrivers(Long seasonId) {
        List<DriverDto> rows = seasonId == null
                ? driverRepository.findAllDrivers()
                : driverRepository.findDriversBySeason(seasonId);

        return rows.stream()
                .map(d -> new DriverDto(d.id(), d.displayName(), d.countryCode(), d.pictureKey(),
                        SlugUtils.toSlug(d.displayName())))
                .toList();
    }

    @Transactional(readOnly = true)
    public DriverProfileDto getProfile(String slug) {
        List<Driver> allDrivers = driverRepository.findAll();

        Driver driver = null;
        String displayName = null;

        for (Driver d : allDrivers) {
            String name = d.getDisplayName();
            if (name == null) {
                name = driverIdentityRepository.findPrimaryByDriverId(d.getId())
                        .map(DriverIdentity::getName)
                        .orElse(null);
            }
            if (slug.equals(SlugUtils.toSlug(name))) {
                driver = d;
                displayName = name;
                break;
            }
        }

        if (driver == null) {
            throw new ResponseStatusException(NOT_FOUND, "DRIVER NOT FOUND: " + slug);
        }

        List<RaceResult> results = raceResultRepository.findAllByDriverId(driver.getId());

        if (results.isEmpty()) {
            return new DriverProfileDto(driver.getId(), displayName, slug,
                    driver.getCountryCode(), driver.getPictureKey(), driver.getNumber(),
                    0, 0, 0, 0, null, null);
        }

        Map<Long, List<Integer>> pointsBySeason = new LinkedHashMap<>();
        int podiums = 0;
        int bestFinish = Integer.MAX_VALUE;
        int bestGrid = Integer.MAX_VALUE;

        for (RaceResult rr : results) {
            Long seasonId = rr.getRace().getSeason().getId();
            pointsBySeason.computeIfAbsent(seasonId, k -> new ArrayList<>()).add(rr.getPoints());

            if (rr.getFinishPosition() <= 3) podiums++;
            if (rr.getFinishPosition() < bestFinish) bestFinish = rr.getFinishPosition();
            if (rr.getGridPosition() < bestGrid) bestGrid = rr.getGridPosition();
        }

        int careerPoints = 0;
        for (Map.Entry<Long, List<Integer>> entry : pointsBySeason.entrySet()) {
            List<Integer> pts = new ArrayList<>(entry.getValue());
            int dropRounds = seasonRepository.findById(entry.getKey())
                    .map(s -> s.getDropRounds()).orElse(0);
            pts.sort(Integer::compareTo);
            int total = pts.stream().mapToInt(Integer::intValue).sum();
            for (int i = 0; i < dropRounds && i < pts.size(); i++) total -= pts.get(i);
            careerPoints += total;
        }

        int finishCount = 0, gridCount = 0;
        for (RaceResult rr : results) {
            if (rr.getFinishPosition() == bestFinish) finishCount++;
            if (rr.getGridPosition() == bestGrid) gridCount++;
        }

        int championships = (int) seasonRepository.countByChampionDriverId(driver.getId());

        return new DriverProfileDto(
                driver.getId(), displayName, slug,
                driver.getCountryCode(), driver.getPictureKey(), driver.getNumber(),
                results.size(), careerPoints, podiums, championships,
                new PositionStatDto(bestFinish, finishCount),
                new PositionStatDto(bestGrid, gridCount)
        );
    }
}
