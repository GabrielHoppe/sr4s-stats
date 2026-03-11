package solo.sr4s_stats.service.importing;

import org.springframework.stereotype.Service;
import solo.sr4s_stats.repository.SeasonRepository;

@Service
public class SyncActiveSeasonsService {

    private final SeasonRepository seasonRepository;
    private final SeasonSyncService seasonSyncService;

    public SyncActiveSeasonsService(
            SeasonRepository seasonRepository,
            SeasonSyncService seasonSyncService
    ) {
        this.seasonRepository = seasonRepository;
        this.seasonSyncService = seasonSyncService;
    }

    public void syncActiveSeasons() {
        var activeSeasons = seasonRepository.findAllByActiveTrue();

        for (var season : activeSeasons) {
            seasonSyncService.syncSeason(season);
        }
    }
}