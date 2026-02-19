package solo.sr4s_stats.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import solo.sr4s_stats.repository.RaceRepository;

@Service
public class AdminRaceService {
    private final RaceRepository raceRepository;

    public AdminRaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Transactional
    public void deleteRace(Long raceId) {
        if (!raceRepository.existsById(raceId)) {
            throw new ResponseStatusException(NOT_FOUND, "RACE NOT FOUND WITH ID: " + raceId);
        }
        raceRepository.deleteById(raceId);
    }
}
