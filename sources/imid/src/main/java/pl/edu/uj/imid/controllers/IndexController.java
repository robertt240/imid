package pl.edu.uj.imid.controllers;

import org.opencv.core.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.uj.imid.services.ObjectDetectionService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class IndexController {
    ObjectDetectionService service;

    @Autowired
    public IndexController(ObjectDetectionService service) {
        this.service = service;
    }

    @RequestMapping("/")
    public String getIndex() {
        return "index";
    }

    @RequestMapping("/upload")
    public String upload(@RequestParam("imagefile") MultipartFile[] files) throws InterruptedException {
        try {
            files[0].transferTo(new File("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img1.jpg"));
            files[1].transferTo(new File("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        service.processImages();
        Thread.sleep(1000);
        return "redirect:/result";
    }
}
