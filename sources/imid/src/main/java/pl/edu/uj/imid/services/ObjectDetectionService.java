package pl.edu.uj.imid.services;


import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.SVM;
import org.opencv.ml.SVMSGD;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjectDetectionService {

    public void processImages() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat img1 = Imgcodecs.imread("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img1.jpg");
        Mat img2 = Imgcodecs.imread("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/uploads/img2.jpg");

        MatOfKeyPoint matOfKeyPoint1 = new MatOfKeyPoint();
        MatOfKeyPoint matOfKeyPoint2 = new MatOfKeyPoint();

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);

        detector.detect(img1, matOfKeyPoint1);
        detector.detect(img2, matOfKeyPoint2);

        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        extractor.compute(img1, matOfKeyPoint1, descriptors1);
        extractor.compute(img2, matOfKeyPoint2, descriptors2);

        if (descriptors1.empty() || descriptors2.empty()) {
            throw new CvException("wrond desc");
        }
//        descriptors1.convertTo(descriptors1, CvType.CV_32F);
//        descriptors2.convertTo(descriptors2, CvType.CV_32F);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        List<MatOfDMatch> matches = new ArrayList<>();

        matcher.knnMatch(descriptors1, descriptors2, matches, 2);

//        double minDist = Double.MAX_VALUE;
//        double maxDist = Double.MIN_VALUE;
//        for (int i = 0; i < descriptors1.rows(); i++) {
//            double dist = matches.toList().get(i).distance;
//            if (dist < minDist) minDist = dist;
//            if (dist > maxDist) maxDist = dist;
//        }

        List<DMatch> goodMatches = new LinkedList<>();

        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).toList().get(0).distance < 0.8 * matches.get(i).toList().get(1).distance) {
                goodMatches.add(matches.get(i).toList().get(0));
            }
        }
//        matches.fromList(goodMatches);

//        System.out.println(matches.toList().size());
        System.out.println(goodMatches.size());
//        System.out.println(matches.toList().stream().mapToDouble(a -> a.distance).sum()/ (double) matches.toList().size());



        MatOfDMatch goodMatchesMat = new MatOfDMatch();

        goodMatchesMat.fromList(goodMatches);

        Mat matchesImage = new Mat();

        MatOfPoint2f mat1 = new MatOfPoint2f();
        MatOfPoint2f mat2 = new MatOfPoint2f();


        Features2d.drawMatches(img1, matOfKeyPoint1, img2, matOfKeyPoint2, goodMatchesMat, matchesImage);
//        MatOfPoint2f o1 = new MatOfPoint2f();
//        o1.fromList(matOfKeyPoint1.toList().stream().map(a -> a.pt).collect(Collectors.toList()));
//        MatOfPoint2f o2 = new MatOfPoint2f();
//        o2.fromList(matOfKeyPoint2.toList().stream().map(a -> a.pt).collect(Collectors.toList()));


//        Mat output = Calib3d.findHomography(o1, o2, Calib3d.RANSAC, 20);


//        Mat output = new Mat();
//        Features2d.drawKeypoints(img1, matOfKeyPoint, output);

//        System.out.println(output.width() + " " + output.height());
//        Imgproc.warpPerspective(img1, output, new Size(img2.(), img2.height()));

        Imgcodecs.imwrite("/home/robert/Documents/IndividualProject/imid/sources/imid/src/main/resources/public/output.jpg", matchesImage);

    }

}
