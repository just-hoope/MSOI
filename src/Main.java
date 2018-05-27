import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {

        rotate(); //dop

        bmpToRGB();         //3
        bmpToYCbCr();       //6
        YCbCrToRGB();       //7
        correlation();       //4


        decim(2);//       //8
        restorPSNR(2);    //9_10

        decim(4);       //8
        restorPSNR(4);    //9_10

        buildHist();             //12
        bitCompressYCbCr();             //13

        task14_15_16();       //14_15_16


    }

    public static void correlation() throws IOException{

        BufferedImage image = Util.read();

        double r_RB = Util.corr(image, "red", "blue");

        System.out.println("r(R, B): "+r_RB);

        double r_RG = Util.corr(image, "red", "green");

        System.out.println("r(R, G): "+r_RG);

        double r_BG = Util.corr(image, "blue", "green");

        System.out.println("r(B, G): "+r_BG);

        int step = 8;

        System.out.println("blue");

        System.out.println("x\ty=0\ty=5\ty=10");

        for(int x = 0; x < 512/4; x+=step){

            double y_0 = Util.autocorr(image, "blue", x, 0);

            double y_5 = Util.autocorr(image, "blue", x, 5);

            double y_10 = Util.autocorr(image, "blue", x, 10);

            System.out.println(x+"\t"+y_0+"\t"+y_5+"\t"+y_10);
        }

        System.out.println("red");

        System.out.println("x\ty=0\ty=5\ty=10");

        for(int x = 0; x < 512/4; x+=step){

            double y_0 = Util.autocorr(image, "red", x, 0);

            double y_5 = Util.autocorr(image, "red", x, 5);

            double y_10 = Util.autocorr(image, "red", x, 10);

            System.out.println(x+"\t"+y_0+"\t"+y_5+"\t"+y_10);
        }

        System.out.println("green");

        System.out.println("x\ty=0\ty=5\ty=10");

        for(int x = 0; x < 512/4; x+=step){

            double y_0 = Util.autocorr(image, "green", x, 0);

            double y_5 = Util.autocorr(image, "green", x, 5);

            double y_10 = Util.autocorr(image, "green", x, 10);

            System.out.println(x+"\t"+y_0+"\t"+y_5+"\t"+y_10);
        }
    }

    public static void bmpToRGB() throws IOException {

        BufferedImage imRed = Util.read();

        //for Red

        Util.applyMask(imRed, 0xff0000);

        Util.save("Red", imRed);

        //for Green

        BufferedImage imGreen = Util.read();

        Util.applyMask(imGreen, 0x00ff00);

        Util.save("Green", imGreen);

        //for Blue

        BufferedImage imBlue = Util.read();

        Util.applyMask(imBlue, 0x0000ff);

        Util.save("Blue", imBlue);
    }

    public static void bmpToYCbCr() throws IOException {

        BufferedImage im = Util.read();

        BufferedImage imY = Util.read();

        BufferedImage imCb = Util.read();

        BufferedImage imCr = Util.read();

        for (int i = 0; i < im.getWidth(); i++) {

            for (int j = 0; j < im.getHeight(); j++) {

                int color = im.getRGB(i, j);

                int R = Util.getI(color, "red");

                int G = Util.getI(color, "green");

                int B = Util.getI(color, "blue");

                int Y = (int) Math.round((0.299 * R) + (0.587 * G) + (0.114 * B));

                int Cb = (int) Math.round((0.5643 * (B - ((0.299 * R) + (0.587 * G) + (0.114 * B))) + 128));

                int Cr = (int) Math.round(0.7132 * (R - ((0.299 * R) + (0.587 * G) + (0.114 * B))) + 128);

                Y = Y + (Y << 8) + (Y << 16);

                Cb = Cb + (Cb << 8) + (Cb << 16);

                Cr = Cr + (Cr << 8) + (Cr << 16);

                imY.setRGB(i, j, Y);

                imCb.setRGB(i, j, Cb);

                imCr.setRGB(i, j, Cr);


            }
        }

        Util.save("Y", imY);

        Util.save("Cb", imCb);

        Util.save("Cr", imCr);

    }

    public static void YCbCrToRGB() throws IOException {

        BufferedImage imY = Util.readFrom("Y");

        BufferedImage imCb = Util.readFrom("Cb");

        BufferedImage imCr = Util.readFrom("Cr");

        BufferedImage im = Util.YCbCrToRgb(imY, imCb, imCr);

        Util.save("restored", im);

        BufferedImage imOrig = Util.read();

        System.out.println("__________Before___________");

        System.out.print("PSNR red: ");

        System.out.println(Util.PSNR(imOrig, im, "red"));


        System.out.print("PSNR green: ");

        System.out.println(Util.PSNR(imOrig, im, "green"));


        System.out.print("PSNR blue: ");

        System.out.println(Util.PSNR(imOrig, im, "blue"));

    }

    public static void decim (int coeff) throws IOException {


        BufferedImage imCb = Util.readFrom("Cb");

        BufferedImage imCr = Util.readFrom("Cr");

        BufferedImage imCbDecimA = Util.decimA(imCb, coeff);

        Util.save("CbDecimA_" + coeff, imCbDecimA);

        BufferedImage imCrDecimA = Util.decimA(imCr, coeff);

        Util.save("CrDecimA_" + coeff, imCrDecimA);

        BufferedImage imCbDecimB = Util.decimB(imCb, coeff);

        Util.save("CbDecimB_" + coeff, imCbDecimB);

        BufferedImage imCrDecimB = Util.decimB(imCr, coeff);

        Util.save("CrDecimB_" + coeff, imCrDecimB);
    }

    public static void restorPSNR(int coeff) throws IOException {

        BufferedImage imCbDecimB = Util.readFrom("CbDecimB_" + coeff);

        BufferedImage imCrDecimB = Util.readFrom("CrDecimB_" + coeff);


        BufferedImage imCbEnlargedB = Util.enlarge(imCbDecimB, coeff);

        Util.save("imCbEnlargedB_" + coeff, imCbEnlargedB);

        BufferedImage imCrEnlargedB = Util.enlarge(imCrDecimB, coeff);

        Util.save("imCrEnlargedB_" + coeff, imCrEnlargedB);


        BufferedImage imCbDecimA = Util.readFrom("CbDecimA_" + coeff);

        BufferedImage imCrDecimA = Util.readFrom("CrDecimA_" + coeff);


        BufferedImage imCbEnlargedA = Util.enlarge(imCbDecimA, coeff);

        Util.save("imCbEnlargedA_" + coeff, imCbEnlargedA);

        BufferedImage imCrEnlargedA = Util.enlarge(imCrDecimA, coeff);

        Util.save("imCrEnlargedA_" + coeff, imCrEnlargedA);


        BufferedImage imY = Util.readFrom("Y");

        BufferedImage imCbOrig = Util.readFrom("Cb");

        BufferedImage imCrOrig = Util.readFrom("Cr");


        BufferedImage restoredB = Util.YCbCrToRgb(imY, imCbEnlargedB, imCrEnlargedB);

        Util.save("restoredB_" + coeff, restoredB);

        BufferedImage imOrig = Util.read();

        System.out.println("_______After for B___________");

        System.out.println("Coeff = " + coeff);

        System.out.print("PSNR red: ");

        System.out.println(Util.PSNR(imOrig, restoredB, "red"));


        System.out.print("PSNR green: ");

        System.out.println(Util.PSNR(imOrig, restoredB, "green"));


        System.out.print("PSNR blue: ");

        System.out.println(Util.PSNR(imOrig, restoredB, "blue"));


        System.out.print("PSNR Cb: ");

        System.out.println(Util.PSNR(imCbOrig, imCbEnlargedB, "blue"));

        System.out.print("PSNR Cr: ");

        System.out.println(Util.PSNR(imCrOrig, imCrEnlargedB, "blue"));


        BufferedImage restoredA = Util.YCbCrToRgb(imY, imCbEnlargedA, imCrEnlargedA);

        Util.save("restoredA_" + coeff, restoredA);


        System.out.println("_______After for A___________");

        System.out.println("Coeff = " + coeff);

        System.out.print("PSNR red: ");

        System.out.println(Util.PSNR(imOrig, restoredA, "red"));


        System.out.print("PSNR green: ");

        System.out.println(Util.PSNR(imOrig, restoredA, "green"));


        System.out.print("PSNR blue: ");

        System.out.println(Util.PSNR(imOrig, restoredA, "blue"));


        System.out.print("PSNR Cb: ");

        System.out.println(Util.PSNR(imCbOrig, imCbEnlargedA, "blue"));

        System.out.print("PSNR Cr: ");

        System.out.println(Util.PSNR(imCrOrig, imCrEnlargedA, "blue"));
    }

    public static void buildHist() throws IOException {

        BufferedImage imOrig = Util.read();

        BufferedImage imY = Util.readFrom("Y");

        BufferedImage imCb = Util.readFrom("Cb");

        BufferedImage imCr = Util.readFrom("Cr");

        System.out.println("____red____");

        Util.hist(imOrig, "red", "redHist", true);

        //Util.hist(imOrig, "red", "rgbHist", false);

        System.out.println("____green____");

        Util.hist(imOrig, "green", "greenHist", true);

        Util.hist(imOrig, "green", "rgbHist", false);

        System.out.println("_____blue____");

        Util.hist(imOrig, "blue", "blueHist", true);

        Util.hist(imOrig, "blue", "rgbHist", false);

        System.out.println("____Y____");

        Util.hist(imY, "", "YHist", true);

        System.out.println("____Cb____");

        Util.hist(imCb, "", "CbHist", true);

        System.out.println("____Cr____");

        Util.hist(imCr, "", "CrHist", true);
    }

    public static void bitCompressYCbCr() throws IOException {

        BufferedImage imOrig = Util.read();

        BufferedImage imY = Util.readFrom("Y");

        BufferedImage imCb = Util.readFrom("Cb");

        BufferedImage imCr = Util.readFrom("Cr");

        System.out.println("____red____");

        System.out.println(Util.H_(imOrig, "red"));

        System.out.println("____green____");

        System.out.println(Util.H_(imOrig, "green"));

        System.out.println("_____blue____");

        System.out.println(Util.H_(imOrig, "blue"));

        System.out.println("____Y____");

        System.out.println(Util.H_(imY, ""));

        System.out.println("____Cb____");

        System.out.println(Util.H_(imCb, ""));

        System.out.println("____Cr____");

        System.out.println(Util.H_(imCr, ""));
    } // кол-во бит на пиксель

    public static void task14_15_16() throws IOException{

        BufferedImage im = Util.read();

        System.out.println(Util.H_(im, "red"));

        System.out.println(Util.H_(im, "green"));

        System.out.println(Util.H_(im, "blue"));

        BufferedImage imY = Util.readFrom("Y");

        System.out.println(Util.H_(imY, ""));

        BufferedImage imCb = Util.readFrom("Cb");

        System.out.println(Util.H_(imCb, ""));

        BufferedImage imCr = Util.readFrom("Cr");

        System.out.println(Util.H_(imCr, ""));

        BufferedImage imOrig = Util.read();

        System.out.println("________D_Y1________");

        int[][] D_Y1 = Util.D(imY, "", 1);

        Util.hist(D_Y1, "", "D_Y1_Hist");

        System.out.println(Util.H_(D_Y1, ""));

        System.out.println("________D_Y2________");

        int[][] D_Y2 = Util.D(imY, "", 2);

        Util.hist(D_Y2, "", "D_Y2_Hist");

        System.out.println(Util.H_(D_Y2, ""));

        System.out.println("________D_Y3________");

        int[][] D_Y3 = Util.D(imY, "", 3);

        Util.hist(D_Y3, "", "D_Y3_Hist");

        System.out.println(Util.H_(D_Y3, ""));

        System.out.println("________D_Y4________");

        int[][] D_Y4 = Util.D(imY, "", 4);

        Util.hist(D_Y4, "", "D_Y4_Hist");

        System.out.println(Util.H_(D_Y4, ""));

        System.out.println("________D_Cb1________");

        int[][] D_Cb1 = Util.D(imCb, "", 1);

        Util.hist(D_Cb1, "", "D_Cb1_Hist");

        System.out.println(Util.H_(D_Cb1, ""));

        System.out.println("________D_Cb2________");

        int[][] D_Cb2 = Util.D(imCb, "", 2);

        Util.hist(D_Cb2, "", "D_Cb2_Hist");

        System.out.println(Util.H_(D_Cb2, ""));

        System.out.println("________D_Cb3________");

        int[][] D_Cb3 = Util.D(imCb, "", 3);

        Util.hist(D_Cb3, "", "D_Cb3_Hist");

        System.out.println(Util.H_(D_Cb3, ""));

        System.out.println("________D_Cb4________");

        int[][] D_Cb4 = Util.D(imCb, "", 4);

        Util.hist(D_Cb4, "", "D_Cb4_Hist");

        System.out.println(Util.H_(D_Cb4, ""));

        System.out.println("________D_Cr1________");

        int[][] D_Cr1 = Util.D(imCr, "", 1);

        System.out.println(Util.H_(D_Cr1, ""));

        Util.hist(D_Cr1, "", "D_Cr1_Hist");

        System.out.println("________D_Cr2________");

        int[][] D_Cr2 = Util.D(imCr, "", 2);

        Util.hist(D_Cr2, "", "D_Cr2_Hist");

        System.out.println(Util.H_(D_Cr2, ""));

        System.out.println("________D_Cr3________");

        int[][] D_Cr3 = Util.D(imCr, "", 3);

        Util.hist(D_Cr3, "", "D_Cr3_Hist");

        System.out.println(Util.H_(D_Cr3, ""));

        System.out.println("________D_Cr4________");

        int[][] D_Cr4 = Util.D(imCr, "", 4);

        Util.hist(D_Cr4, "", "D_Cr4_Hist");

        System.out.println(Util.H_(D_Cr4, ""));

        System.out.println("________D_R1________");

        int[][] D_R1 = Util.D(imOrig, "red", 1);

        Util.hist(D_R1, "red", "D_R1_Hist");

        System.out.println(Util.H_(D_R1, ""));

        System.out.println("________D_R2________");

        int[][] D_R2 = Util.D(imOrig, "red", 2);

        Util.hist(D_R2, "red", "D_R2_Hist");

        System.out.println(Util.H_(D_R2, ""));

        System.out.println("________D_R3________");

        int[][] D_R3 = Util.D(imOrig, "red", 3);

        Util.hist(D_R3, "red", "D_R3_Hist");

        System.out.println(Util.H_(D_R3, ""));

        System.out.println("________D_R4________");

        int[][] D_R4 = Util.D(imOrig, "red", 4);

        Util.hist(D_R4, "red", "D_R4_Hist");

        System.out.println(Util.H_(D_R4, ""));

        System.out.println("________D_B1________");

        int[][] D_B1 = Util.D(imOrig, "blue", 1);

        Util.hist(D_B1, "blue", "D_B1_Hist");

        System.out.println(Util.H_(D_B1, ""));

        System.out.println("________D_B2________");

        int[][] D_B2 = Util.D(imOrig, "blue", 2);

        Util.hist(D_B2, "blue", "D_B2_Hist");

        System.out.println(Util.H_(D_B2, ""));

        System.out.println("________D_B3________");

        int[][] D_B3 = Util.D(imOrig, "blue", 3);

        Util.hist(D_B3, "blue", "D_B3_Hist");

        System.out.println(Util.H_(D_B3, ""));

        System.out.println("________D_B4________");

        int[][] D_B4 = Util.D(imOrig, "blue", 4);

        Util.hist(D_B4, "blue", "D_B4_Hist");

        System.out.println(Util.H_(D_B4, ""));

        System.out.println("________D_G1________");

        int[][] D_G1 = Util.D(imOrig, "green", 1);

        Util.hist(D_G1, "green", "D_G1_Hist");

        System.out.println(Util.H_(D_G1, ""));

        System.out.println("________D_G2________");

        int[][] D_G2 = Util.D(imOrig, "green", 2);

        Util.hist(D_B2, "green", "D_G2_Hist");

        System.out.println(Util.H_(D_G2, ""));

        System.out.println("________D_G3________");

        int[][] D_G3 = Util.D(imOrig, "green", 3);

        Util.hist(D_G3, "green", "D_G3_Hist");

        System.out.println(Util.H_(D_G3, ""));

        System.out.println("________D_G4________");

        int[][] D_G4 = Util.D(imOrig, "green", 4);

        Util.hist(D_G4, "green", "D_G4_Hist");

        System.out.println(Util.H_(D_G4, ""));


    }

    public static void rotate() throws IOException { //повернуть на 270 против часовой = 90 по часовой

        BufferedImage imOrig = Util.read();

        int H = imOrig.getHeight();

        int W = imOrig.getWidth();

        BufferedImage imRotated = new BufferedImage(H, W, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < W; x++) {

            for (int y = 0; y < H; y++) {


                int x_ = H - y - 1;

                int y_ = x;

                imRotated.setRGB(x_, y_, imOrig.getRGB(x, y));

            }
        }

        Util.save("rotated", imRotated);

    }

}

