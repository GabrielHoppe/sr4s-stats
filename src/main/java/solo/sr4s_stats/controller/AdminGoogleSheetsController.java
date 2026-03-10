package solo.sr4s_stats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solo.sr4s_stats.service.GoogleSheetsService;

import java.util.List;

@RestController
@RequestMapping("/admin/google-sheets")
public class AdminGoogleSheetsController {
    private final GoogleSheetsService googleSheetsService;

    public AdminGoogleSheetsController(GoogleSheetsService googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
    }

    @GetMapping("/preview")
    public List<List<Object>> previewSheet(
            @RequestParam String spreadsheetId,
            @RequestParam String range
    ) {
        return googleSheetsService.readSheet(spreadsheetId, range);
    }
}