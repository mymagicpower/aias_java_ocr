package top.aias.ocr;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.opencv.OpenCVImageFactory;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import top.aias.ocr.utils.common.ImageUtils;
import top.aias.ocr.utils.detection.OcrV3Detection;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * OCR文字检测(V3).
 * OCR Detection(V3)
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 */
public final class OcrV3DetExample {

    private static final Logger logger = LoggerFactory.getLogger(OcrV3DetExample.class);

    private OcrV3DetExample() {
    }

    public static void main(String[] args) throws IOException, ModelException, TranslateException {
        Path imageFile = Paths.get("src/test/resources/led.jpg");
        Image image = OpenCVImageFactory.getInstance().fromFile(imageFile);

        OcrV3Detection detection = new OcrV3Detection();
        try (ZooModel detectionModel = ModelZoo.loadModel(detection.ledDetCriteria());
             Predictor<Image, NDList> detector = detectionModel.newPredictor();
             NDManager manager = NDManager.newBaseManager();) {

            NDList dt_boxes = detector.predict(image);
            // 交给 NDManager自动管理内存
            // attach to manager for automatic memory management
            dt_boxes.attach(manager);

            for (int i = 0; i < dt_boxes.size(); i++) {
                ImageUtils.drawRect((Mat) image.getWrappedImage(), dt_boxes.get(i));
            }
            ImageUtils.saveImage(image, "detect_rect.png", "build/output");
            ((Mat) image.getWrappedImage()).release();
        }
    }
}
