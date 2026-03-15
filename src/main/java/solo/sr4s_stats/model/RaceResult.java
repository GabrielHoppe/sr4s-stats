package solo.sr4s_stats.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "race_results",
        uniqueConstraints = @UniqueConstraint(name = "uq_race_results_race_driver", columnNames = {"race_id", "driver_id"})
)
public class RaceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "car_number", nullable = false)
    private int carNumber;

    @Column(name = "gap_to_leader", nullable = false, length = 50)
    private String gapToLeader;

    @Column(name = "grid_position", nullable = false)
    private int gridPosition;

    @Column(name = "finish_position", nullable = false)
    private int finishPosition;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "dnf", nullable = false)
    private boolean dnf;

    @Column(name = "fastest_lap", nullable = false)
    private boolean fastestLap;

    public static RaceResult create(Race race, Driver driver) {
        RaceResult rr = new RaceResult();
        rr.race = race;
        rr.driver = driver;
        return rr;
    }

    public Race getRace(){
        return race;
    }

    public Driver getDriver(){
        return driver;
    }

    public void setCarNumber(int carNumber) {
        this.carNumber = carNumber;
    }

    public void setGapToLeader(String gapToLeader) {
        this.gapToLeader = gapToLeader;
    }

    public void setGridPosition(int gridPosition) {
        this.gridPosition = gridPosition;
    }

    public int getFinishPosition(){
        return finishPosition;
    }

    public void setFinishPosition(int finishPosition) {
        this.finishPosition = finishPosition;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setDnf(boolean dnf) {
        this.dnf = dnf;
    }

    public void setFastestLap(boolean fastestLap) {
        this.fastestLap = fastestLap;
    }

    public int getGridPosition() { return gridPosition; }
}
