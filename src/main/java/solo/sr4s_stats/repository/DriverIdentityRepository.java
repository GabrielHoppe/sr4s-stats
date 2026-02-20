package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.DriverIdentity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DriverIdentityRepository extends JpaRepository<DriverIdentity, Long> {
    Optional<DriverIdentity> findByIracingId(Integer iracingId);
    Optional<DriverIdentity> findByName(String name);

    List<DriverIdentity> findAllByDriverId(Long driverId);
    Optional<DriverIdentity> findByDriverIdAndIsPrimaryTrue(Long driverId);
    Optional<DriverIdentity> findByIdAndDriverId(Long id, Long driverId);
    Optional<DriverIdentity> findByDriverIdAndIracingId(Long driverId, Integer iracingId);

    boolean existsByDriverIdAndIsPrimaryTrue(Long driverId);
    boolean existsByDriverIdAndIracingId(Long driverId, Integer iracingId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update DriverIdentity di
        set di.isPrimary = false
        where di.driver.id = :driverId
          and di.isPrimary = true
    """)
    int unsetPrimaryForDriver(@Param("driverId") Long driverId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update DriverIdentity di
        set di.driver.id = :winnerId
        where di.driver.id = :loserId
    """)
    int moveIdentities(@Param("loserId") Long loserId,
                       @Param("winnerId") Long winnerId);
}
