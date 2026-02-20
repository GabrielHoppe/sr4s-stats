package solo.sr4s_stats.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "driver_identities")
public class DriverIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "iracing_id")
    private Integer iracingId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public static DriverIdentity primary(Driver driver, Integer iracingId, String name){
        DriverIdentity di = new DriverIdentity();
        di.driver = driver;
        di.iracingId = iracingId;
        di.name = name;
        di.isPrimary = true;
        return di;
    }

    public Driver getDriver() {
        return driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Integer getIracingId() {
        return iracingId;
    }

    public String getName() {
        return name;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Long getId() {
        return id;
    }
}
