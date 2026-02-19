package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.Season;

import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findBySpreadsheetId(String spreadsheetId);
}
