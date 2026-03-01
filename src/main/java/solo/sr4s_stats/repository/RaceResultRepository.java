package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.RaceResult;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    Optional<RaceResult> findByRaceIdAndDriverId(Long raceId, Long driverId);

    List<RaceResult> findAllByDriverId(Long driverId);

    boolean existsByRaceIdAndDriverId(Long raceId, Long driverId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update RaceResult rr
        set rr.driver.id = :winnerId
        where rr.driver.id = :loserId
    """)
    int moveResults(@Param("loserId") Long loserId,
                    @Param("winnerId") Long winnerId);
}
