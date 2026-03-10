package solo.sr4s_stats.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import solo.sr4s_stats.config.GoogleSheetsProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
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

    public List<List<Object>> readSheet(String spreadsheetId, String range){
        try {
            Sheets sheets = buildSheetsClient();

            ValueRange response = sheets.spreadsheets()
                    .values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            return values == null ? Collections.emptyList() : values;
        } catch (IOException | GeneralSecurityException e){
            throw new RuntimeException("FAILED TO READ GOOGLE SHEETS",e);
        }
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
