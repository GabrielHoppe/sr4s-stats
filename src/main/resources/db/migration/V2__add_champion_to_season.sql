ALTER TABLE seasons
    ADD COLUMN champion_driver_id BIGINT,
    ADD CONSTRAINT fk_seasons_champion_driver
        FOREIGN KEY (champion_driver_id) REFERENCES drivers(id)
        ON DELETE SET NULL;
