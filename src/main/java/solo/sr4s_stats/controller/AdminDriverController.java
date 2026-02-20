package solo.sr4s_stats.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solo.sr4s_stats.dto.MergeDriversRequest;
import solo.sr4s_stats.service.AdminDriverService;

@RestController
@RequestMapping("/admin")
public class AdminDriverController {
    private final AdminDriverService adminDriverService;

    public AdminDriverController(AdminDriverService adminDriverService) {
        this.adminDriverService = adminDriverService;
    }

    @PostMapping("/drivers/merge")
    public ResponseEntity<Void> mergeDrivers(@Valid @RequestBody MergeDriversRequest req) {
        adminDriverService.mergeDrivers(req.winnerDriverId(), req.loserDriverId());
        return ResponseEntity.noContent().build();
    }
}
