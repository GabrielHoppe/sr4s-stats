package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.sr4s_stats.model.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {}
