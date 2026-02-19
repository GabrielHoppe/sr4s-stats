package solo.sr4s_stats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solo.sr4s_stats.service.AdminRaceService;

@RestController
@RequestMapping("/admin")
public class AdminRaceController {
    private final AdminRaceService adminRaceService;

    public AdminRaceController(AdminRaceService adminRaceService) {
        this.adminRaceService = adminRaceService;
    }

    @DeleteMapping("/races/{raceId}")
    public ResponseEntity<Void> deleteRace(@PathVariable Long raceId){
        adminRaceService.deleteRace(raceId);
        return ResponseEntity.noContent().build();
    }

}
