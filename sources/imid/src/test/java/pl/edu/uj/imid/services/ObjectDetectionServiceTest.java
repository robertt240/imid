package pl.edu.uj.imid.services;

import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectDetectionServiceTest {
    ObjectDetectionService objectDetectionService = new ObjectDetectionService();

    @Test
    public void testService(){
        objectDetectionService.processImages();
    }
}