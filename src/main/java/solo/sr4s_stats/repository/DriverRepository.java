package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solo.sr4s_stats.dto.DriverDto;
import solo.sr4s_stats.model.Driver;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("""
        select new solo.sr4s_stats.dto.DriverDto(
            d.id,
            COALESCE(d.displayName, di.name),
            d.countryCode,
            d.pictureKey
        )
        from Driver d
        left join DriverIdentity di
            on di.driver = d and di.isPrimary = true
        order by COALESCE(d.displayName, di.name)
    """)
    List<DriverDto> findAllDrivers();

    @Query("""
        select distinct new solo.sr4s_stats.dto.DriverDto(
            d.id,
            COALESCE(d.displayName, di.name),
            d.countryCode,
            d.pictureKey
        )
        from RaceResult rr
        join rr.driver d
        join rr.race r
        left join DriverIdentity di
            on di.driver = d and di.isPrimary = true
        where r.season.id = :seasonId
        order by COALESCE(d.displayName, di.name)
    """)
    List<DriverDto> findDriversBySeason(@Param("seasonId") Long seasonId);
}
