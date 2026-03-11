package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.RaceResult;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    Optional<RaceResult> findByRaceIdAndDriverId(Long raceId, Long driverId);

    List<RaceResult> findAllByDriverId(Long driverId);

    List<RaceResult> findAllByRaceId(Long raceId);

    boolean existsByRaceIdAndDriverId(Long raceId, Long driverId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update RaceResult rr
        set rr.driver.id = :winnerId
        where rr.driver.id = :loserId
    """)
    int moveResults(@Param("loserId") Long loserId,
                    @Param("winnerId") Long winnerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from RaceResult rr
        where rr.race.id = :raceId
          and rr.driver.id not in :driverIds
    """)
    int deleteByRaceIdAndDriverIdNotIn(@Param("raceId") Long raceId,
                                       @Param("driverIds") Collection<Long> driverIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from RaceResult rr
        where rr.race.id = :raceId
    """)
    int deleteAllByRaceId(@Param("raceId") Long raceId);
}
