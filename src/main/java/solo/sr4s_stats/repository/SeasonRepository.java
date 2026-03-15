package solo.sr4s_stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solo.sr4s_stats.dto.SeasonListDto;
import solo.sr4s_stats.model.Season;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findBySpreadsheetId(String spreadsheetId);

    boolean existsByYearAndSubYearSeason(int year, int subYearSeason);

    List<Season> findAllByActiveTrue();

    long countByChampionDriverId(Long driverId);

    @Query("""
    select new solo.sr4s_stats.dto.SeasonListDto(
        s.id,
        s.name,
        s.year,
        s.subYearSeason,
        s.dropRounds,
        s.active,
        count(r)
    )
    from Season s
    left join Race r on r.season.id = s.id
    group by s.id, s.name, s.year, s.subYearSeason, s.dropRounds, s.active
    order by s.year desc, s.subYearSeason desc
""")
    List<SeasonListDto> findAllSeasonSummaries();
}
