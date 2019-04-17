package app.metatron.discovery.domain.ipm.common.util.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

/**
 * 미디어 유틸
 * 이미지 리사이즈 & 크롭
 */
@Component
public class MediaUtil {

    public static final String MEDIA_TYPE_LARGE = "L";
    public static final String MEDIA_TYPE_THUMBNAIL = "T";

    public static final String MEDIA_TYPE_PROFILE = "P";

    public static final String MEDIA_TYPE_COMMUNICATION = "C";

    private static final int LARGE_WIDTH = 800;
    private static final int LARGE_HEIGHT = 500;
    private static final int THUMBNAIL_WIDTH = 240;
    private static final int THUMBNAIL_HEIGHT = 150;

    private static final int PROFILE_SQUARE = 120;

    private static final int COMM_WIDTH = 255;
    private static final int COMM_HEIGHT = 158;

    private static final String EXT_JPG = "jpg";
    private static final String EXT_JPEG = "jpeg";
    private static final String EXT_PNG = "png";
    private static final String EXT_BMP = "bmp";
    private static final String EXT_GIF = "gif";

    private static List<String> ImageTypes;

    static {
        ImageTypes = new ArrayList<>();
        ImageTypes.add(EXT_JPG);
        ImageTypes.add(EXT_JPEG);
        ImageTypes.add(EXT_PNG);
        ImageTypes.add(EXT_BMP);
//        ImageTypes.add(EXT_GIF);
    }

    /**
     * 미디어 수용 가능 여부
     * @param extension
     * @return
     */
    public boolean acceptable(String extension) {
        return ImageTypes.contains(extension.toLowerCase());
    }

    /**
     * 커뮤니케이션용 대표이미지 가공
     * @param original
     * @param type
     * @return
     * @throws Exception
     */
    public File transformCommunication(File original, String type) throws Exception {
        String originPath = original.getAbsolutePath();

        File commFile = new File(originPath + "_" + MEDIA_TYPE_COMMUNICATION);

        BufferedImage originImage = ImageIO.read(original);

        BufferedImage commImage = resizeAndCropImage(originImage, COMM_WIDTH, COMM_HEIGHT);

        ImageIO.write(commImage, type, commFile);

        return commFile;
    }

    /**
     * 사용자 프로필용 가공
     * @param original
     * @param type
     * @return
     * @throws Exception
     */
    public File transformProfile(File original, String type) throws Exception {
        String originPath = original.getAbsolutePath();

        File profileFile = new File(originPath + "_" + MEDIA_TYPE_PROFILE);

        BufferedImage originImage = ImageIO.read(original);

        BufferedImage profileImage = resizeAndCropImage(originImage, PROFILE_SQUARE, PROFILE_SQUARE);

        ImageIO.write(profileImage, type, profileFile);

        return profileFile;
    }


    /**
     * 분석/리포트 앱 용도 가공
     * @param original
     * @param type
     * @return
     * @throws Exception
     */
    public Map<String, File> transform(File original, String type) throws Exception {
        Map<String, File> transformedImages = new HashMap<>();

        String originPath = original.getAbsolutePath();

        File thumbFile = new File(originPath + "_" + MEDIA_TYPE_THUMBNAIL);
        File largeFile = new File(originPath + "_" + MEDIA_TYPE_LARGE);

        BufferedImage originImage = ImageIO.read(original);

        BufferedImage largeImage = resizeAndCropImage(originImage, LARGE_WIDTH, LARGE_HEIGHT);
        ImageIO.write(largeImage, type, largeFile);

        // tumbnail resize
        BufferedImage thumbImage = resizeAndCropImage(largeImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        ImageIO.write(thumbImage, type, thumbFile);

        transformedImages.put(MEDIA_TYPE_LARGE, largeFile);
        transformedImages.put(MEDIA_TYPE_THUMBNAIL, thumbFile);

        return transformedImages;
    }

    /**
     * 리사이즈 및 크롭 처리
     * @param originImage
     * @param width
     * @param height
     * @return
     */
    private BufferedImage resizeAndCropImage( BufferedImage originImage, int width, int height ) {
        int originWidth = originImage.getWidth();
        int originHeight = originImage.getHeight();

        Scalr.Mode mode = (double) width / (double) height >= (double) originWidth / (double) originHeight ? Scalr.Mode.FIT_TO_WIDTH
                : Scalr.Mode.FIT_TO_HEIGHT;

        BufferedImage resizedImage = Scalr.resize(originImage, Scalr.Method.ULTRA_QUALITY, mode, width, height);

        int x = 0;
        int y = 0;

        if (mode == Scalr.Mode.FIT_TO_WIDTH) {
            y = (resizedImage.getHeight() - height) / 2;
        } else if (mode == Scalr.Mode.FIT_TO_HEIGHT) {
            x = (resizedImage.getWidth() - width) / 2;
        }

        return Scalr.crop(resizedImage, x, y, width, height);
    }

}
