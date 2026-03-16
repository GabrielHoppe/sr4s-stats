package solo.sr4s_stats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solo.sr4s_stats.config.AuthProperties;
import solo.sr4s_stats.dto.LoginRequest;
import solo.sr4s_stats.dto.LoginResponse;
import solo.sr4s_stats.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthProperties authProperties;
    private final JwtService jwtService;

    public AuthController(AuthProperties authProperties, JwtService jwtService) {
        this.authProperties = authProperties;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        boolean valid = authProperties.getAdmin().getUsername().equals(req.username())
                && authProperties.getAdmin().getPassword().equals(req.password());

        if (!valid) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new LoginResponse(jwtService.generate(req.username())));
    }
}
