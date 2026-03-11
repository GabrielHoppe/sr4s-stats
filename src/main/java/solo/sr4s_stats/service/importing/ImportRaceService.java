package solo.sr4s_stats.service.importing;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.sr4s_stats.model.*;
import solo.sr4s_stats.repository.*;
import solo.sr4s_stats.service.importing.model.ParsedRow;
import solo.sr4s_stats.service.importing.model.ParsedSheet;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

@Service
public class ImportRaceService {

    private final RaceRepository races;
    private final DriverRepository drivers;
    private final DriverIdentityRepository identities;
    private final RaceResultRepository results;
    private final JdbcTemplate jdbc;

    public ImportRaceService(
            RaceRepository races,
            DriverRepository drivers,
            DriverIdentityRepository identities,
            RaceResultRepository results,
            JdbcTemplate jdbc
    ) {
        this.races = races;
        this.drivers = drivers;
        this.identities = identities;
        this.results = results;
        this.jdbc = jdbc;
    }

    @Transactional
    public void importParsedSheet(Season season, ParsedSheet sheet){
        Race race = races.findBySeasonIdAndRoundNumber(season.getId(), sheet.roundNumber())
                .orElseGet(() -> races.save(Race.create(season, sheet.roundNumber(), sheet.roundTitle(), sheet.circuit(), sheet.raceDate())));

        race.setName(sheet.roundTitle());
        race.setCircuit(sheet.circuit());
        race.setRaceDate(sheet.raceDate());
        race = races.save(race);

        Integer fastestIdx = findFastestRowIndex(sheet.rows());

        List<Long> importedDriversIds = new ArrayList<>();

        for (int i = 0; i < sheet.rows().size(); i++) {
            ParsedRow row = sheet.rows().get(i);

            Driver driver = resolveDriver(row);
            importedDriversIds.add(driver.getId());

            upsertRaceResult(race, driver, row, fastestIdx != null && i == fastestIdx);
        }

        if (importedDriversIds.isEmpty()){
            results.deleteAllByRaceId(race.getId());
        } else {
            results.deleteByRaceIdAndDriverIdNotIn(race.getId(), importedDriversIds);
        }

        recalcDriverNumbersForRace(race.getId());
    }

    private Driver resolveDriver(ParsedRow row) {
        if (row.iracingId() != null){
            var byIracing = identities.findByIracingId(row.iracingId());
            if (byIracing.isPresent()) return byIracing.get().getDriver();
        }

        var byName = identities.findByName(row.driverName());
        if (byName.isPresent()) return byName.get().getDriver();

        Driver d = drivers.save(new Driver());
        identities.save(DriverIdentity.primary(d, row.iracingId(), row.driverName()));
        return d;
    }

    private void upsertRaceResult(Race race, Driver driver, ParsedRow row, boolean fastestLap) {
        RaceResult rr = results.findByRaceIdAndDriverId(race.getId(), driver.getId())
                .orElseGet(() -> RaceResult.create(race, driver));

        rr.setFinishPosition(row.finishPos());
        rr.setCarNumber(row.carNumber());
        rr.setGapToLeader(row.gapToLeader());
        rr.setGridPosition(row.startPos());
        rr.setPoints(row.points());
        rr.setDnf(!"Running".equalsIgnoreCase(row.dnfStatus()));
        rr.setFastestLap(fastestLap);

        results.save(rr);
    }

    private Integer findFastestRowIndex(List<ParsedRow> rows) {
        record Candidate(int idx, Duration time) {}
        Candidate best = null;

        for (int i = 0; i < rows.size(); i++) {
            String raw = rows.get(i).fastestTimeRaw();
            if (raw == null || raw.isBlank()) continue;

            Duration d = parseLapTime(raw);
            Candidate c = new Candidate(i, d);

            if (best == null || c.time().compareTo(best.time()) < 0) best = c;
        }

        return best == null ? null : best.idx();
    }

    private Duration parseLapTime(String s) {
        String t = s.trim();
        String[] parts = t.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("INVALID TIME:" + s);

        long minutes = Long.parseLong(parts[0]);
        String[] secMs = parts[1].split("\\.");
        long seconds = Long.parseLong(secMs[0]);

        long millis = 0;
        if (secMs.length > 1) {
            String ms = (secMs[1] + "000").substring(0, 3);
            millis = Long.parseLong(ms);
        }

        return Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(millis);
    }

    private void recalcDriverNumbersForRace(Long raceId) {
        String sql = """
                WITH affected AS (
                    SELECT DISTINCT driver_id
                    FROM race_results
                    WHERE race_id = ?
                ),
                last_numbers AS (
                    SELECT DISTINCT ON (rr.driver_id)
                    rr.driver_id,
                    rr.car_number
                    FROM race_results rr
                    JOIN races r ON r.id = rr.race_id
                    JOIN affected a ON a.driver_id = rr.driver_id
                    ORDER BY rr.driver_id, r.race_date DESC, rr.id DESC
                )
                UPDATE drivers d
                SET number = ln.car_number
                FROM last_numbers ln
                WHERE d.id = ln.driver_id
                """;
        jdbc.update(sql, raceId);
    }
}
