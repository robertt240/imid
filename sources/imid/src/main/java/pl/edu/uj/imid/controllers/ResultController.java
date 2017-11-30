package pl.edu.uj.imid.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class ResultController {

    @RequestMapping("/result")
    public String getResult() {
        return "result";
    }

    @RequestMapping(value = "image/{imageName}")
    @ResponseBody
    public byte[] getImage(@PathVariable(value = "imageName") String imageName) throws IOException {

        File serverFile =
                new File("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/public/" + imageName + ".jpg");

        return Files.readAllBytes(serverFile.toPath());
    }

}
