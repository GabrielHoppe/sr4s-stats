SET search_path TO public;


CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    number int,
    display_name VARCHAR(255),
    picture_key VARCHAR(255),
    country_code CHAR(3),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_drivers_picture_key_not_blank
        CHECK (picture_key IS NULL OR length(trim(picture_key)) > 0)
);

CREATE TABLE driver_identities (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    iracing_id INT,
    name VARCHAR(255) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_driver_identities_driver
        FOREIGN KEY (driver_id) REFERENCES drivers(id)
        ON DELETE RESTRICT
);

CREATE UNIQUE INDEX uq_driver_identities_one_primary_per_driver
    ON driver_identities (driver_id)
    WHERE is_primary = true;

CREATE INDEX idx_driver_identities_driver_id
    ON driver_identities (driver_id);

CREATE INDEX idx_driver_identities_name
    ON driver_identities (name);

CREATE UNIQUE INDEX uq_driver_identities_iracing_id_not_null
    ON driver_identities (iracing_id)
    WHERE iracing_id IS NOT NULL;

CREATE TABLE seasons (
    id BIGSERIAL PRIMARY KEY,
    spreadsheet_id VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    year INT NOT NULL,
    sub_year_season INT NOT NULL,
    name VARCHAR(255),

    CONSTRAINT uq_seasons_year_subseason UNIQUE (year, sub_year_season)
);

CREATE INDEX idx_seasons_active
    ON seasons (active);

CREATE TABLE races (
    id BIGSERIAL PRIMARY KEY,
    season_id BIGINT NOT NULL,
    name VARCHAR(255),
    circuit VARCHAR(255),
    race_date DATE NOT NULL,

    CONSTRAINT fk_races_season
        FOREIGN KEY (season_id) REFERENCES seasons(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_races_season_id
    ON races (season_id);

CREATE INDEX idx_races_race_date
    ON races (race_date);

CREATE TABLE race_results (
    id BIGSERIAL PRIMARY KEY,
    race_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    car_number INT NOT NULL,
    gap_to_leader VARCHAR(50) NOT NULL,
    grid_position INT NOT NULL,
    finish_position INT NOT NULL,
    points INT NOT NULL,
    dnf BOOLEAN NOT NULL DEFAULT FALSE,
    fastest_lap BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_race_results_race
        FOREIGN KEY (race_id) REFERENCES races(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_race_results_driver
        FOREIGN KEY (driver_id) REFERENCES drivers(id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_race_results_race_driver UNIQUE (race_id, driver_id),

    CONSTRAINT ck_race_results_car_number_positive CHECK (car_number > 0),
    CONSTRAINT ck_race_results_grid_positive CHECK (grid_position > 0),
    CONSTRAINT ck_race_results_finish_positive CHECK (finish_position > 0),
    CONSTRAINT ck_race_results_points_nonnegative CHECK (points >= 0)
);

CREATE INDEX idx_race_results_race_id
    ON race_results (race_id);

CREATE INDEX idx_race_results_driver_id
    ON race_results (driver_id);

CREATE INDEX idx_race_results_driver_race
    ON race_results (driver_id, race_id);