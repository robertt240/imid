package pl.edu.uj.imid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.uj.imid.services.ObjectDetectionService;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
public class IndexController {
    private final ObjectDetectionService service;


    @Autowired
    public IndexController(ObjectDetectionService service) {
        this.service = service;
    }

    @RequestMapping("/")
    public String getIndex() {
        return "index";
    }

    @RequestMapping("/upload")
    public String upload(
            @RequestParam("imagefile") MultipartFile[] files,
            RedirectAttributes redirectAttributes) throws InterruptedException, NoSuchAlgorithmException {
        try {
            files[0].transferTo(new File("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img1.jpg"));
            files[1].transferTo(new File("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        double fingerprint1 = service.findFingerprint("img1");
        double fingerprint2 = service.findFingerprint("img2");
        redirectAttributes.addFlashAttribute("img1", String.valueOf(fingerprint1));
        redirectAttributes.addFlashAttribute("img2", String.valueOf(fingerprint2));
        redirectAttributes.addFlashAttribute("rate",
                "Same object probability = " +
                        String.valueOf(100 * (1 -
                                (int) Math.abs(fingerprint1 - fingerprint2) / (fingerprint1 + fingerprint2))) + "%");
        Thread.sleep(1000);
        return "redirect:/result";
    }
}
