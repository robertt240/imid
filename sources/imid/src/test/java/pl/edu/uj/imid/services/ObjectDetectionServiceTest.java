package pl.edu.uj.imid.services;

import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ObjectDetectionServiceTest {
    ObjectDetectionService objectDetectionService = new ObjectDetectionService();

    @Test
    public void testService() throws NoSuchAlgorithmException {
        objectDetectionService.findFingerprint("test4_2");
    }

    @Test
    public void testFindingPoints() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img1 = Imgcodecs.imread("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img1.jpg");
        Mat grayImage = new Mat();
        Imgproc.cvtColor(img1, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat detectedEdges = new Mat();
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

        Imgproc.Canny(detectedEdges, detectedEdges, 50, 50 * 3, 3, false);

        Mat dest = new Mat();
        Core.add(dest, Scalar.all(0), dest);
        img1.copyTo(dest, detectedEdges);

        Mat gray = detectedEdges;
//        System.out.println(gray.cols());
        Point pt2 = new Point(gray.cols() - 1, 260);
        Point pt1 = new Point(0, 385);
        List<Point> result = objectDetectionService.getPointsOnLine(pt1, pt2, false);
//        System.out.println(result);
//
        System.out.println(objectDetectionService.getGrayPointsVariance(result, gray));
        System.out.println(objectDetectionService.listToString(result, gray));
        Imgproc.line(img1, pt1, pt2, new Scalar(0, 255, 0), 3);
        Imgproc.line(img1, new Point(0, 100), new Point(gray.cols() - 1, 20), new Scalar(0, 255, 0), 1);

        Imgcodecs.imwrite("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/public/output_test.jpg", img1);
        Imgcodecs.imwrite("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/public/output_edges_test.jpg", detectedEdges);


    }
}