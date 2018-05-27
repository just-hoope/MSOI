import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Util {

    public static final String DIR = "/home/nadezhda/Рабочий стол/dir/";

    public static int getI(int c, String color){

        if(color.equals("red")){

            c = c & 0xFF0000;

            c = c >> 16;
        }

        if (color.equals("green")){

            c = c & 0x00FF00;

            c = c >> 8;
        }

        if (color.equals("blue") || color.equals("")){

            c = c & 0x0000FF;

        }

        return c;
    }

    public static double M_1(BufferedImage image, String color, int x, int y){

        double result = 0;

        int H = image.getHeight();

        int W = image.getWidth();

        for(int i = 0; i < W-x; i++) {

            for (int j = 0; j < H-y; j++) {

                int c = image.getRGB(i, j);

                c = getI(c, color);

                result = result + c;

            }
        }

        result = result /((W-x) * (H-y));

        return result;
    }

    public static double M_2(BufferedImage image, String color, int x, int y){

        double result = 0;

        int H = image.getHeight();

        int W = image.getWidth();

        for(int i = x; i < W; i++) {

            for (int j = y; j < H; j++) {

                int c = image.getRGB(i, j);

                c = getI(c, color);

                result = result + c;

            }
        }

        result = result /((W-x) * (H-y));

        return result;
    }

    public static double M(BufferedImage image, String color){

        double result = 0;

        int H = image.getHeight();

        int W = image.getWidth();

        for(int i = 0; i < W; i++){

            for (int j = 0; j < H; j++){

                int c = image.getRGB(i, j);

                c = getI(c, color);

                result = result + c;

            }
        }

        result = result /(W * H);

        return result;
    }

    public static double sigma(BufferedImage image, String color){

        double result = 0;

        int H = image.getHeight();

        int W = image.getWidth();

        double M = M(image, color);

        for(int i = 0; i < W; i++) {

            for (int j = 0; j < H; j++) {

                int c = image.getRGB(i, j);

                c = getI(c, color);

                double tmp = c - M;

                tmp = tmp * tmp;

                tmp = tmp / (W * H - 1);

                result = result + tmp;

            }
        }

        return Math.sqrt(result);
    }

    public static double sigma_1(BufferedImage image, String color, int x, int y){

        double result = 0;

        int H = image.getHeight();

        int W = image.getWidth();

        double M = M_1(image, color, x, y);

        for(int i = 0; i < W-x; i++) {

            for (int j = 0; j < H-y; j++) {

                int c = image.getRGB(i, j);

                c = getI(c, color);

                double tmp = c - M;

                tmp = tmp * tmp;

                tmp = tmp / ((W-x) * (H-y) - 1);

                result = result + tmp;

            }
        }

        return Math.sqrt(result);
    }

    public static double sigma_2(BufferedImage image, String color, int x, int y){

        double result = 0;

        int H = image.getHeight();

        int W = image.getWidth();

        double M = M_2(image, color, x, y);

        for(int i = x; i < W; i++) {

            for (int j = y; j < H; j++) {

                int c = image.getRGB(i, j);

                c = getI(c, color);

                double tmp = c - M;

                tmp = tmp * tmp;

                tmp = tmp / ((W-x) * (H-y) - 1);

                result = result + tmp;

            }
        }

        return Math.sqrt(result);
    }

    public static double autocorr(BufferedImage image, String color, int x, int y) throws  IOException{

        int H = image.getHeight();

        int W = image.getWidth();

        double sigmaA = sigma_1(image, color,  x, y);

        double sigmaB = sigma_2(image, color, x, y);

        double sigma = sigmaA * sigmaB;


        double M_A = M_1(image, color, x, y);

        double M_B = M_2(image, color, x, y);

        double sum = 0;

        for (int i = 0; i < image.getWidth() - x; i++) {

            for (int j = 0; j < image.getHeight() - y; j++) {

                sum+=(getI(image.getRGB(i, j), color) - M_A)*(getI(image.getRGB(i+x, j+y), color) - M_B);
            }
        }


        double tmp = sum/(image.getHeight()-y)/(image.getWidth()-x);

        return tmp/sigma;
    }

    public static double corr(BufferedImage image, String A, String B) throws  IOException{

        double sigmaA = sigma(image, A);

        double sigmaB = sigma(image, B);

        double sigma = sigmaA * sigmaB;


        double M_A = M(image, A);

        double M_B = M(image, B);


        int H = image.getHeight();

        int W = image.getWidth();

        double sum = 0;

        for (int i = 0; i < image.getWidth(); i++) {

            for (int j = 0; j < image.getHeight(); j++) {

                sum+=(getI(image.getRGB(i, j), A) - M_A)*(getI(image.getRGB(i, j), B) - M_B);
            }
        }


        double tmp = sum/image.getHeight()/image.getWidth();

        return tmp/sigma;
    }

    public static double corr2(BufferedImage imageA,  String A, BufferedImage imageB,String B) throws  IOException{

        double sigmaA = sigma(imageA, A);

        double sigmaB = sigma(imageB, B);

        double sigma = sigmaA * sigmaB;


        double M_A = M(imageA, A);

        double M_B = M(imageB, B);


        int H = imageA.getHeight();

        int W = imageA.getWidth();

        double sum = 0;

        for (int i = 0; i < imageA.getWidth(); i++) {

            for (int j = 0; j < imageA.getHeight(); j++) {

                sum+=(getI(imageA.getRGB(i, j), A) - M_A)*(getI(imageB.getRGB(i, j), B) - M_B);
            }
        }


        double tmp = sum/imageA.getHeight()/imageA.getWidth();

        return tmp/sigma;
    }

    public static BufferedImage read() throws IOException {

        String fileName = "/home/nadezhda/Рабочий стол/Lenna.bmp";

        File bmpFile = new File(fileName);

        BufferedImage image = ImageIO.read(bmpFile);
        return image;
    }

    public static BufferedImage readFrom(String name) throws IOException {

        String fileName = DIR+name+".bmp";

        File bmpFile = new File(fileName);

        BufferedImage image = ImageIO.read(bmpFile);

        return image;
    }

    public static void save(String name, BufferedImage image) throws IOException{

        String filename = DIR + name + ".bmp";

        ImageIO.write(image, "BMP", new File(filename));

    }

    public static void applyMask(BufferedImage im, int mask){

        for (int i = 0; i < im.getWidth(); i++){

            for(int j = 0; j < im.getHeight(); j++){

                int color = im.getRGB(i, j);

                color = color & mask;

                im.setRGB(i, j, color);

            }
        }
    }

    public static double PSNR(BufferedImage orig, BufferedImage restored, String color){

        double result = 0;

        int H = orig.getHeight();

        int W = orig.getWidth();

        int L = 8;

        for(int i = 0; i < W; i++){

            for (int j = 0; j < H; j++){

                int colorOrig = orig.getRGB(i, j);

                int colorRestored = restored.getRGB(i, j);

                int origI = getI(colorOrig, color);

                int restoredI = getI(colorRestored, color);

                int tmp = origI - restoredI;

                tmp = tmp * tmp;

                result = result + tmp;

            }
        }

        //System.out.println(result);

        //System.out.println("tmp: " + ( ((long)(1 << L) - 1) * ((1 << L) - 1)));

        result = ( W * H * ((long)(1 << L) - 1) * ((1 << L) - 1)) / result;

        //System.out.println(result);

        result = 10 * Math.log10(result);

        return result;
    }

    public static boolean imageCompare(BufferedImage imageOne, BufferedImage imageTwo){

        boolean result = true;
        int c = 0;

        if (imageOne.getWidth() != imageTwo.getWidth() || imageOne.getHeight() != imageTwo.getHeight()){

            result = false;

        }

        for (int i = 0; i < imageOne.getWidth(); i++){

            for(int j = 0; j < imageTwo.getHeight(); j++){

                int colorOne = imageOne.getRGB(i, j);

                int colorTwo = imageTwo.getRGB(i, j);

                if (colorOne != colorTwo){

                    result = false;
                    c++;
//                    System.out.println(colorOne);
//
//                    System.out.println(colorTwo);

                }
            }

        }

        System.out.println(c);
        System.out.println(result);

        return result;
    }

    public static int clip(int x) {

        if (x < 0) {

            return 0;
        }

        else if (x > 255) {

            return 255;
        }

        else {

            return x;
        }
    }

    public static int rgb(int r, int g, int b) {

        return (255 << 24) + (r << 16) + (g << 8) + b;
    }

    public static BufferedImage decimA(BufferedImage im, int coeff){

        BufferedImage result = new BufferedImage(im.getWidth()/coeff, im.getHeight()/coeff, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < result.getWidth(); i++){

            for(int j = 0; j < result.getHeight(); j++){

               int color = im.getRGB(i*coeff, j*coeff);

               result.setRGB(i, j, color);
            }
        }

        return  result;
    }

    public static BufferedImage decimB(BufferedImage im, int coeff){

        BufferedImage result = new BufferedImage(im.getWidth()/coeff, im.getHeight()/coeff, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < result.getWidth(); i++){

            for(int j = 0; j < result.getHeight(); j++){

                int count = 1;

                int color = im.getRGB(i*coeff, j*coeff) & 0x0000FF;

                if((i*coeff - 1) >= 0){

                    count++;

                    color = color + (im.getRGB(i*coeff - 1, j*coeff) & 0x0000FF);
                }

                if((j*coeff - 1) >= 0) {

                    count++;

                    color = color + (im.getRGB(i*coeff, j*coeff - 1) & 0x0000FF);

                }

                if((i*coeff+1) < im.getWidth()){

                    count++;

                    color = color + (im.getRGB(i*coeff+1, j*coeff) & 0x0000FF);
                }

                if((j*coeff+1) < im.getHeight()){

                    count++;

                    color = color + (im.getRGB(i*coeff, j*coeff+1) & 0x0000FF);
                }

                color = color/count;

                color = (color + (color << 8) + (color << 16));

                result.setRGB(i, j, color);
            }
        }

        return  result;
    }

    public static BufferedImage enlarge(BufferedImage im, int coeff){

        BufferedImage result = new BufferedImage(im.getWidth()*coeff, im.getHeight()*coeff, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < result.getWidth(); i++){

            for(int j = 0; j < result.getHeight(); j++){

                int color = im.getRGB(i/coeff, j/coeff);

                result.setRGB(i, j, color);
            }
        }

        return  result;
    }

    public static BufferedImage YCbCrToRgb(BufferedImage imY, BufferedImage imCb, BufferedImage imCr){

        BufferedImage im = new BufferedImage(imY.getWidth(), imY.getHeight(), BufferedImage.TYPE_INT_RGB);


        for (int i = 0; i < im.getWidth(); i++){

            for(int j = 0; j < im.getHeight(); j++) {

                int Y = (imY.getRGB(i, j) & 0x0000FF);

                int Cb = (imCb.getRGB(i, j) & 0x0000FF);

                int Cr =(imCr.getRGB(i, j) & 0x0000FF);

                int R = clip(Y + (int)Math.round(1.402 * (Cr - 128)));

                int G = clip(Y + (int)Math.round( -0.714136  * (Cr - 128) - 0.334136 * (Cb - 128)));

                int B = clip(Y +(int)Math.round( 1.772 * (Cb - 128)));

                int color = (B + (G << 8) + (R << 16));

                im.setRGB(i, j, color);
            }
        }

        return im;
    }

    public static void printHist(int[] res, String color, String fileName, boolean fill)throws  IOException{

        int c = 0xF000FF;

        if (color.equals("red")){

            c = 0xFF0000;
        }

        if (color.equals("blue")){

            c = 0x0000FF;
        }

        if (color.equals("green")){

            c = 0x00FF00;
        }

        int max = 0;

        for(int i = 0; i < res.length; i++){

            if (res[i] > max){

                max = res[i];
            }

        }


        int histSize = 150;

        boolean first = false;

        BufferedImage imHist;

        if(new File(DIR + fileName + ".bmp").exists()){

            imHist = readFrom(fileName);

        } else {

            first = true;

        }


        if (res.length <= 256){

        imHist = readFrom("hist");

        }
        else {

            imHist = readFrom("hist512");

        }


        int shift = 29;

        for (int i = 0; i < res.length; i++){

            for(int j = 0; j < histSize; j++){

                if(!fill) {

                    if (Math.abs((res[i] * histSize) / max - j) < 3) {

                        imHist.setRGB(i+shift, histSize - j - 1, c);

                    } else if (first) {

                        imHist.setRGB(i+shift, histSize - j - 1, 0xFFFFFF);
                    }
                } else{

                    if ((res[i] * histSize) / max > j ) {

                        imHist.setRGB(i+shift, histSize - j - 1, c);

                    } else {

                        imHist.setRGB(i+shift, histSize - j - 1, 0xFFFFFF);
                    }
                }
            }
        }


        save(fileName, imHist);

    }

    public static void hist(BufferedImage im, String color, String fileName, boolean fill) throws  IOException{

        int step = 1;

        int[] res = new int[256/step];

        for (int i = 0; i <res.length; i++){

            res[i] = 0;
        }

        for (int i = 0; i < im.getWidth(); i++){

            for(int j = 0; j < im.getHeight(); j++){

               int c = im.getRGB(i, j);

               c = getI(c, color);

               res[c/step]++;
            }
        }


      printHist(res, color, fileName, fill);

    }

    public static void hist(int[][] im, String color, String fileName) throws  IOException{

        int step = 1;

        int[] res = new int[512/step];

        for (int i = 0; i < res.length; i++){

            res[i] = 0;
        }

        for (int i = 0; i < im.length; i++){

            for(int j = 0; j < im[i].length; j++){

                int c = im[i][j];

                res[(255+c)/step]++;
            }
        }

       printHist(res, color, fileName, true);

    }

    public static double H_(BufferedImage im, String color){

        int[] res = new int[256];

        for (int i = 0; i < res.length; i++){

            res[i] = 0;
        }

        for (int i = 0; i < im.getWidth(); i++){

            for(int j = 0; j < im.getHeight(); j++){

                int c = im.getRGB(i, j);

                c = getI(c, color);

                res[c]++;
            }
        }

        double H_ = 0;

        double n = im.getHeight()*im.getWidth();


        for (int i = 0; i < res.length; i++){

            if(res[i]!=0) {

                H_ = H_ + res[i] / (n) * (Math.log(res[i] / (n)) / Math.log(2));
            }
        }

        return - H_;
    }

    public static double H_(int[][] im, String color){

        int[] res = new int[256];

        for (int i = 0; i < res.length; i++){

            res[i] = 0;
        }

        for (int i = 0; i < im.length; i++){

            for(int j = 0; j < im[i].length; j++){

                int c = im[i][j];

                c = getI(c, color);

                res[c]++;
            }
        }

        double H_ = 0;

        double n = im.length*im.length;


        for (int i = 0; i < res.length; i++){

            if(res[i]!=0) {

                H_ = H_ + res[i] / (n) * (Math.log(res[i] / (n)) / Math.log(2));
            }
        }

        return - H_;
    }

    public static int[][] D(BufferedImage im, String color, int r){

        int[][] D = new int[im.getWidth()][im.getHeight()];

        for (int i = 1; i < im.getWidth(); i++){

            for(int j = 1; j < im.getHeight(); j++){

                int a = im.getRGB(i, j);

                a = getI(a, color);

                if(r == 1){

                    int tmp1 = im.getRGB(i, j - 1);

                    tmp1 = getI(tmp1, color);

                    D[i][j] = a - tmp1;
                }

                if(r == 2){

                    int tmp2 = im.getRGB(i - 1, j);

                    tmp2 = getI(tmp2, color);

                    D[i][j] = a - tmp2;
                }

                if(r == 3){

                    int tmp3 = im.getRGB(i - 1, j - 1);

                    tmp3 = getI(tmp3, color);

                    D[i][j] = a - tmp3;
                }

                if(r == 4) {

                    int tmp1 = im.getRGB(i, j - 1);

                    tmp1 = getI(tmp1, color);

                    int tmp2 = im.getRGB(i - 1, j);

                    tmp2 = getI(tmp2, color);

                    int tmp3 = im.getRGB(i - 1, j - 1);

                    tmp3 = getI(tmp3, color);

                    int tmp = (tmp1 + tmp2 + tmp3) / 3;

                    D[i][j] = a - tmp;
                }
            }
        }

        int[][] res = new int[im.getWidth()-1][im.getHeight()-1];

        for (int i = 1; i < im.getWidth(); i++){

            for(int j = 1; j < im.getHeight(); j++){

                res[i-1][j-1] = D[i][j];
            }
        }

        return res;
    }


}
