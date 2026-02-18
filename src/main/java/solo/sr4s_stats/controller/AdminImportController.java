package solo.sr4s_stats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solo.sr4s_stats.dto.ImportRaceResultsRequest;
import solo.sr4s_stats.service.importing.ImportRaceService;
import solo.sr4s_stats.service.importing.SheetParser;

@RestController
@RequestMapping("/admin/import")
public class AdminImportController {
    private final SheetParser parser;
    private final ImportRaceService importer;

    public AdminImportController(SheetParser parser, ImportRaceService importer) {
        this.parser = parser;
        this.importer = importer;
    }

    @PostMapping("/race-results")
    public ResponseEntity<?> importRaceResults(@RequestBody ImportRaceResultsRequest req) {
        var parsed = parser.parse(req.values());
        importer.importParsedSheet(parsed);
        return ResponseEntity.ok().build();
    }
}
