package solo.sr4s_stats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerHome {
    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui/index.html";
    }
}
