package solo.sr4s_stats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.sr4s_stats.dto.DriverDto;
import solo.sr4s_stats.repository.DriverRepository;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository){
        this.driverRepository = driverRepository;
    }

    @Transactional(readOnly = true)
    public List<DriverDto> getDrivers(Long seasonId) {
        if (seasonId == null) return driverRepository.findAllDrivers();
        return driverRepository.findDriversBySeason(seasonId);
    }
}
