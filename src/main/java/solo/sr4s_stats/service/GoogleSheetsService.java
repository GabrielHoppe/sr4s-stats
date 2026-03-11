package solo.sr4s_stats.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import solo.sr4s_stats.config.GoogleSheetsProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {
    private static final String APPLICATION_NAME = "SR4S Stats";
    private static final String SHEETS_SCOPE = "https://www.googleapis.com/auth/spreadsheets.readonly";

    private final GoogleSheetsProperties googleSheetsProperties;

    public GoogleSheetsService(GoogleSheetsProperties googleSheetsProperties) {
        this.googleSheetsProperties = googleSheetsProperties;
    }

    public List<List<Object>> readSheet(String spreadsheetId, String range) {
        try {
            Sheets sheets = buildSheetsClient();

            ValueRange response = sheets.spreadsheets()
                    .values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            return values == null ? Collections.emptyList() : values;
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("FAILED TO READ GOOGLE SHEETS", e);
        }
    }

    public List<String> listSheetTitles(String spreadsheetId) {
        try {
            Sheets sheets = buildSheetsClient();

            Spreadsheet spreadsheet = sheets.spreadsheets()
                    .get(spreadsheetId)
                    .execute();

            if (spreadsheet.getSheets() == null) {
                return Collections.emptyList();
            }

            List<String> titles = new ArrayList<>();
            for (var sheet : spreadsheet.getSheets()) {
                if (sheet.getProperties() != null && sheet.getProperties().getTitle() != null) {
                    titles.add(sheet.getProperties().getTitle());
                }
            }

            return titles;
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("FAILED TO LIST GOOGLE SHEETS", e);
        }
    }

    public String readFirstCell(String spreadsheetId, String sheetTitle) {
        List<List<Object>> values = readSheet(spreadsheetId, quotedSheetTitle(sheetTitle) + "!A1:A1");

        if (values.isEmpty()) {
            return "";
        }
        if (values.get(0).isEmpty()) {
            return "";
        }
        if (values.get(0).get(0) == null) {
            return "";
        }

        return values.get(0).get(0).toString().trim();
    }

    public List<List<String>> readSheetAsStrings(String spreadsheetId, String sheetTitle) {
        List<List<Object>> raw = readSheet(spreadsheetId, quotedSheetTitle(sheetTitle) + "!A1:Z500");

        List<List<String>> converted = new ArrayList<>();

        for (List<Object> row : raw) {
            List<String> convertedRow = new ArrayList<>();
            for (Object cell : row) {
                convertedRow.add(cell == null ? "" : cell.toString());
            }
            converted.add(convertedRow);
        }

        return converted;
    }

    private String quotedSheetTitle(String sheetTitle) {
        return "'" + sheetTitle.replace("'", "''") + "'";
    }

    private Sheets buildSheetsClient() throws IOException, GeneralSecurityException {
        try (InputStream inputStream = new FileInputStream(googleSheetsProperties.getCredentialsPath())) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(Collections.singleton(SHEETS_SCOPE));

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }
}