package solo.sr4s_stats.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import solo.sr4s_stats.model.Season;
import solo.sr4s_stats.repository.SeasonRepository;

@Component
public class DevSeedService implements CommandLineRunner {

    public static final String DEV_SPREADSHEET_ID = "DEV_SHEET";

    private final SeasonRepository seasons;

    public DevSeedService(SeasonRepository seasons) {
        this.seasons = seasons;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seasons.findBySpreadsheetId(DEV_SPREADSHEET_ID).orElseGet(() -> {
            Season s = new Season();
            s.setSpreadsheetId(DEV_SPREADSHEET_ID);
            s.setActive(true);
            s.setYear(2026);
            s.setSubYearSeason(1);
            s.setName("DEV TEST SEASON");
            return seasons.save(s);
        });
    }
}
