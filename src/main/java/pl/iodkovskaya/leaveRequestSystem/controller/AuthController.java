package pl.iodkovskaya.leaveRequestSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class AuthController {
    @RequestMapping("/login")
    public String getLoginPage() {
        return "login";
    }

}
