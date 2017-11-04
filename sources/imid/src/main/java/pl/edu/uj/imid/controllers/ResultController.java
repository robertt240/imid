package pl.edu.uj.imid.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ResultController {

    @RequestMapping("/result")
    public String getResult() {
        return "result";
    }

}
