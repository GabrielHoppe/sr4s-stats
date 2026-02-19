package solo.sr4s_stats.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seasons")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spreadsheet_id", nullable = false)
    private String spreadsheetId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "sub_year_season", nullable = false)
    private int subYearSeason;

    @Column(name = "name")
    private String name;

    public Long getId() {
        return id;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }
    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public int getSubYearSeason() {
        return subYearSeason;
    }
    public void setSubYearSeason(int subYearSeason) {
        this.subYearSeason = subYearSeason;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
