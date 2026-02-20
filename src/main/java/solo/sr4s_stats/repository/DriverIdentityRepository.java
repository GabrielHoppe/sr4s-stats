package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.DriverIdentity;

import java.util.List;
import java.util.Optional;

public interface DriverIdentityRepository extends JpaRepository<DriverIdentity, Long> {
    Optional<DriverIdentity> findByIracingId(Integer iracingId);
    Optional<DriverIdentity> findByName(String name);

    List<DriverIdentity> findAllByDriverId(Long driverId);
    Optional<DriverIdentity> findByDriverIdAndIsPrimaryTrue(Long driverId);
    Optional<DriverIdentity> findByIdAndDriverId(Long id, Long driverId);
    Optional<DriverIdentity> findByDriverIdAndIracingId(Long driverId, Integer iracingId);
}
