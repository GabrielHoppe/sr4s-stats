package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.Race;

import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {
    Optional<Race> findBySeasonIdAndRoundNumber(Long seasonId, Integer roundNumber);
}
