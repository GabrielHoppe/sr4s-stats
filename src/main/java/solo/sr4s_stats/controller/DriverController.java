package solo.sr4s_stats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solo.sr4s_stats.dto.DriverDto;
import solo.sr4s_stats.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("api/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService){
        this.driverService = driverService;
    }

    @GetMapping
    public List<DriverDto> getDrivers(
            @RequestParam(required = false) Long seasonId
    ){
        return driverService.getDrivers(seasonId);
    }

}
