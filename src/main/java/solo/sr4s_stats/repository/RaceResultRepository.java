package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.RaceResult;

import java.util.Optional;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    Optional<RaceResult> findByRaceIdAndDriverId(Long raceId, Long driverId);
}
