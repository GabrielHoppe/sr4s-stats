package solo.sr4s_stats.service.importing;

import org.springframework.stereotype.Component;
import solo.sr4s_stats.service.importing.model.ParsedRow;
import solo.sr4s_stats.service.importing.model.ParsedSheet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

@Component
public class SheetParser {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public ParsedSheet parse(List<List<String>> values) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("SHEET EMPTY");

        String roundTitle = firstCell(values, 0);
        String circuit = firstCell(values, 1);
        String championship = firstCell(values, 2);

        int roundNumber = parseRoundNumber(roundTitle);
        LocalDate raceDate = findRaceDate(values);

        int headerRow = findHeaderRow(values);
        Map<String, Integer> col = headerIndex(values.get(headerRow));

        List<ParsedRow> rows = new ArrayList<>();
        for (int r = headerRow + 1; r < values.size(); r++) {
            List<String> line = values.get(r);
            if (isBlankRow(line)) break;

            String posS = cell(line, col.get("Pos."));
            if (posS.isBlank()) continue;

            int pos = Integer.parseInt(posS);
            int carNo = Integer.parseInt(cell(line, col.get("No.")));
            String name = cell(line, col.get("Driver"));
            String gap = cell(line, col.get("Gap"));
            String fastest = cell(line, col.get("Fastest")); // pode ser ""
            int startPos = Integer.parseInt(cell(line, col.get("Start Pos.")));
            String dnfStatus = cell(line, col.get("DNF"));
            Integer iracingId = parseNullableInt(cell(line, col.get("Iracing ID")));
            Integer pts = parseNullableInt(cell(line, col.get("Pts.")));
            int points = (pts == null) ? 0 : pts;

            rows.add(new ParsedRow(pos, carNo, name, gap, fastest, startPos, dnfStatus, iracingId, points));
        }

        return new ParsedSheet(roundTitle, roundNumber, circuit, championship, raceDate, rows);
    }

    private static int parseRoundNumber(String roundTitle) {
        // "ROUND 1" -> 1
        var m = java.util.regex.Pattern.compile("(?i)ROUND\\s+(\\d+)")
                .matcher(roundTitle == null ? "" : roundTitle.trim());
        if (!m.find()) throw new IllegalArgumentException("ROUND NUMBER NOT FOUND ON: " + roundTitle);
        return Integer.parseInt(m.group(1));
    }

    private static LocalDate findRaceDate(List<List<String>> values) {
        for (int i = 0; i < Math.min(values.size(), 12); i++) {
            String s = firstCell(values, i);
            if (s != null && s.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(s, DATE);
            }
        }
        throw new IllegalArgumentException("RACE DATE NOT FOUND");
    }

    private static int findHeaderRow(List<List<String>> values) {
        for (int i = 0; i < values.size(); i++) {
            List<String> row = values.get(i);
            if (row == null) continue;
            String joined = String.join("|", row);
            if (joined.contains("Pos.") && joined.contains("Driver") && joined.contains("Iracing ID")) return i;
        }
        throw new IllegalArgumentException("HEADER NOT FOUND");
    }

    private static Map<String, Integer> headerIndex(List<String> header) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            String h = header.get(i);
            if (h != null) m.put(h.trim(), i);
        }
        for (String k : List.of("Pos.","No.","Driver","Gap","Fastest","Start Pos.","DNF","Iracing ID","Pts.")) {
            if (!m.containsKey(k)) throw new IllegalArgumentException("COLUMN MISSING: " + k);
        }
        return m;
    }

    private static String firstCell(List<List<String>> values, int row) {
        if (row >= values.size()) return null;
        List<String> r = values.get(row);
        if (r == null || r.isEmpty()) return null;
        return (r.get(0) == null) ? null : r.get(0).trim();
    }

    private static String cell(List<String> row, Integer idx) {
        if (row == null || idx == null || idx >= row.size() || row.get(idx) == null) return "";
        return row.get(idx).trim();
    }

    private static Integer parseNullableInt(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        return Integer.parseInt(t);
    }

    private static boolean isBlankRow(List<String> row) {
        if (row == null || row.isEmpty()) return true;
        for (String s : row) {
            if (s != null && !s.isBlank()) return false;
        }
        return true;
    }
}
