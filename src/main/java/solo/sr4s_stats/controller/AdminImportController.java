package solo.sr4s_stats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solo.sr4s_stats.service.importing.SyncActiveSeasonsService;

@RestController
@RequestMapping("/admin/import")
public class AdminImportController {

    private final SyncActiveSeasonsService syncActiveSeasonsService;

    public AdminImportController(SyncActiveSeasonsService syncActiveSeasonsService) {
        this.syncActiveSeasonsService = syncActiveSeasonsService;
    }

    @PostMapping("/sync-active")
    public ResponseEntity<Void> syncActiveSeasons(){
        syncActiveSeasonsService.syncActiveSeasons();
        return ResponseEntity.noContent().build();
    }
}
