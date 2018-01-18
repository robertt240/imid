package pl.edu.uj.imid.services;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjectDetectionService {


    public List<Point> getPointsOnLine(Point start, Point end, boolean reversed) {

        List<Point> result = new LinkedList<>();
        Double coef = (end.y - start.y) / (end.x - start.x);
        if (start.y == 0 && start.x != 0) {
            int x;
            for (int y = Double.valueOf(start.y).intValue(); y <= Double.valueOf(end.y).intValue(); y++) {
                x = Double.valueOf((y + coef * start.x + start.y) / coef).intValue();
                result.add(new Point(x, y));
            }
        } else {
            int y;
            for (int x = Double.valueOf(start.x).intValue(); x <= Double.valueOf(end.x).intValue(); x++) {
                y = Double.valueOf(coef * (x - start.x) + start.y).intValue();
                result.add(new Point(x, y));
            }
        }
        return result;
    }

    public double getAvgDifferenceOnPoints(List<Point> points, Mat img) {
        double sum = 0;
        double value1 = 0;
        double value2 = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            value1 = img.get(Double.valueOf(points.get(i).x).intValue(), Double.valueOf(points.get(i).y).intValue())[0];
            value2 = img.get(Double.valueOf(points.get(i + 1).x).intValue(), Double.valueOf(points.get(i + 1).y).intValue())[0];
            sum += Math.abs(value2 - value1);
        }
        return sum;
    }

    public double getGrayPointsVariance(List<Point> points, Mat img) {
        double average = points.stream()
                .mapToDouble(a -> img.get(Double.valueOf(a.y).intValue(), Double.valueOf(a.x).intValue())[0])
                .average()
                .getAsDouble();

        double variance = points.stream()
                .mapToDouble(a -> img.get(Double.valueOf(a.y).intValue(), Double.valueOf(a.x).intValue())[0])
                .map(a -> Math.pow(a - 255, 2))
                .average()
                .getAsDouble();

        return variance;
    }

    public String listToString(List<Point> points, Mat img) {
        points.remove(points.size() - 1);
        List<Double> doubles =
                points.stream()
                        .mapToDouble(a -> img.get(
                                Double.valueOf(a.y).intValue(),
                                Double.valueOf(a.x).intValue())[0])
                        .boxed()
                        .collect(Collectors.toList());
        return doubles.toString();
    }

    public List<Integer> getDifferencesOnLine(List<Point> line, Mat img) {
        List<Integer> diffs = new LinkedList<>();
        for (int i = 0; i < line.size() - 2; i++) {
            Point point1 = line.get(i + 1);
            Point point2 = line.get(i);
//            System.out.println(point1 + " " + point2);
            diffs.add(Double.valueOf(img.get((int) point1.y, (int) point1.x)[0] -
                    img.get((int) point2.y, (int) point2.x)[0]).intValue());
        }
        return diffs;
    }

    public double findFingerprint(String fileName) throws NoSuchAlgorithmException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img1 = Imgcodecs.imread("/home/robert/Documents/IndividualProject/" +
                "imid/sources/imid/src/main/resources/uploads/" + fileName + ".jpg");
        Mat grayImage = new Mat();

        Imgproc.cvtColor(img1, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat detectedEdges = new Mat();
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));

        Imgproc.Canny(detectedEdges, detectedEdges, 100, 25 * 3, 3, false);

        Mat dest = new Mat();
        Core.add(dest, Scalar.all(0), dest);
        img1.copyTo(dest, detectedEdges);

        Mat gray = detectedEdges;

        double minavg = Double.MAX_VALUE;
        int miny0 = 0;
        int miny1 = 0;
        int minx0 = 0;
        int minx1 = gray.cols() - 1;

        for (int i = 0; i < gray.rows(); i++) {
            for (int j = 0; j < gray.rows(); j++) {
                List<Point> pointsOnLine = getPointsOnLine(new Point(0, i), new Point(gray.cols() - 1, j), false);
                double result = getGrayPointsVariance(pointsOnLine, gray);
                if (result < minavg) {
                    minavg = result;
                    miny0 = i;
                    miny1 = j;
                }
            }
        }
        Core.rotate(gray, gray, Core.ROTATE_90_CLOCKWISE);
        Core.rotate(img1, img1, Core.ROTATE_90_CLOCKWISE);
        boolean rotateBack = true;
        for (int i = 0; i < gray.rows(); i++) {
            for (int j = 0; j < gray.rows(); j++) {
                List<Point> pointsOnLine = getPointsOnLine(new Point(0, i), new Point(gray.cols() - 1, j), false);
                double result = getGrayPointsVariance(pointsOnLine, gray);
                if (result < minavg) {
                    rotateBack = false;
                    minavg = result;
                    miny0 = i;
                    miny1 = j;
                }
            }
        }
        if (rotateBack) {
            Core.rotate(gray, gray, Core.ROTATE_90_COUNTERCLOCKWISE);
            Core.rotate(img1, img1, Core.ROTATE_90_COUNTERCLOCKWISE);
        }

//        System.out.println(miny0 + " " + miny1);
//        System.out.println(minavg);
//        System.out.println(listToString(getPointsOnLine(
//                new Point(minx0, miny0),
//                new Point(minx1, miny1),
//                false),
//                gray));
        int centerRow = (miny0 + miny1) / 2;
        double diff = gray.rows() / 2 - centerRow;
        double ortCoef = -1 / ((double) (miny1 - miny0) / gray.cols());
        double ortB = gray.rows() / 2 - ortCoef * gray.cols() / 2;

        Point ort0 = ortB < 0 && false ? new Point(0, ortB) : new Point(-ortB / ortCoef, 0);
        Point ort1 = ortCoef * gray.cols() + ortB >= gray.rows() || true ?
                new Point((gray.cols() - ortB) / ortCoef, gray.rows()) :
                new Point(gray.cols(), ortCoef * gray.cols() + ortB);

        Imgproc.line(img1, new Point(minx0, miny0 + diff), new Point(minx1, miny1 + diff), new Scalar(0, 255, 0), 3);
        Imgproc.line(img1, ort0, ort1, new Scalar(255, 0, 0), 3);

        System.out.println(ort0);
        System.out.println(ort1);

        List<Point> pointsOnLine = getPointsOnLine(
                ort0,
                ort1,
                true);
        pointsOnLine.remove(pointsOnLine.size() - 1);
        System.out.println(getDifferencesOnLine(
                pointsOnLine, grayImage).stream().mapToInt(Math::abs).sum());

        System.out.println(listToString(pointsOnLine,
                grayImage));

        System.out.println(getAvgDifferenceOnPoints(pointsOnLine, grayImage));

        Imgcodecs.imwrite("/home/robert/Documents/IndividualProject/imid/sources/" +
                "imid/src/main/resources/public/output_" + fileName + ".jpg", img1);

//        Imgcodecs.imwrite("/home/robert/Documents/IndividualProject/imid/sources/" +
//                "imid/src/main/resources/public/output_edges.jpg", detectedEdges);

        String text = String.valueOf(getAvgDifferenceOnPoints(pointsOnLine, grayImage));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);

        return getAvgDifferenceOnPoints(pointsOnLine, grayImage);
    }

}
