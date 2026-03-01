package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solo.sr4s_stats.repository.DriverIdentityRepository;
import solo.sr4s_stats.repository.DriverRepository;
import solo.sr4s_stats.repository.RaceResultRepository;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AdminDriverService {
    private final DriverRepository driverRepository;
    private final DriverIdentityRepository driverIdentityRepository;
    private final RaceResultRepository raceResultRepository;

    public AdminDriverService(
            DriverRepository driverRepository,
            DriverIdentityRepository driverIdentityRepository,
            RaceResultRepository raceResultRepository
    ) {
        this.driverRepository = driverRepository;
        this.driverIdentityRepository = driverIdentityRepository;
        this.raceResultRepository = raceResultRepository;
    }

    @Transactional
    public void mergeDriver(Long winnerDriverId, Long loserDriverId){
        if (winnerDriverId == null || loserDriverId == null){
            throw new ResponseStatusException(BAD_REQUEST, "BOTH WINNER AND LOSER DRIVERS ID ARE REQUIRED");
        }
        if (winnerDriverId.equals(loserDriverId)){
            throw new ResponseStatusException(BAD_REQUEST, "WINNER AND LOSER DRIVERS ID MUST BE DIFFERENT");
        }
        if (!driverRepository.existsById(winnerDriverId)){
            throw new ResponseStatusException(NOT_FOUND, "WINNER DRIVER NOT FOUND WITH ID: " + winnerDriverId);
        }
        if (!driverRepository.existsById(loserDriverId)){
            throw new ResponseStatusException(NOT_FOUND, "LOSER DRIVER NOT FOUND WITH ID: " + loserDriverId);
        }

        driverIdentityRepository.unsetPrimaryForDriver(loserDriverId);
        driverIdentityRepository.moveIdentities(loserDriverId, winnerDriverId);
        raceResultRepository.moveResults(loserDriverId, winnerDriverId);
        driverRepository.deleteById(loserDriverId);
    }
}
