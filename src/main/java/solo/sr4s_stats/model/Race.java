package solo.sr4s_stats.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "races",
    uniqueConstraints = @UniqueConstraint(name = "uq_races_season_round", columnNames = {"season_id", "round_number"}))
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "circuit")
    private String circuit;

    @Column(name = "race_date", nullable = false)
    private LocalDate raceDate;

    public static Race create(Season season, int roundNumber, String name, String circuit, LocalDate raceDate) {
        Race r = new Race();
        r.season = season;
        r.roundNumber = roundNumber;
        r.name = name;
        r.circuit = circuit;
        r.raceDate = raceDate;
        return r;
    }

    public Long getId() {
        return id;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public LocalDate getRaceDate() {
        return raceDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    public void setRaceDate(LocalDate raceDate) {
        this.raceDate = raceDate;
    }
}
