package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.DriverIdentity;

import java.util.Optional;

public interface DriverIdentityRepository extends JpaRepository<DriverIdentity, Long> {
    Optional<DriverIdentity> findByIracingId(Integer iracingId);
    Optional<DriverIdentity> findByName(String name);
}
