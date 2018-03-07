package com.example.lsnoussi.image_processing20;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import java.util.Random;

/**
 * Created by lsnoussi on 07/03/18.
 */

public class Effects {

    //variables
    public static final int COLOR_MIN = 0x00;
    public static final int COLOR_MAX = 0xFF;


    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  function to gray a bitmap using a tab of pixels */


    public static void toGray(Bitmap bmp){

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[w*h];
        bmp.getPixels(pixels, 0, w, 0,0 ,w,h);
        for(int i =0 ; i < pixels.length; ++i) {
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            int gray = (int) (0.3*r+ 0.59*g+ b*0.11);
            pixels[i] = Color.rgb(gray,gray,gray);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }


    //effects_colorize

    /**

     *  @param bmp
     * take a bitmap as a parameter.
     *  function to put a random colored filter on a bitmap */

    public static void colorize (Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[w * h];

        Random ran = new Random();

        // possibility for hue [0..360]
        int nbr = ran.nextInt(360);
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < h * w; ++i) {
            int p = pixels[i];
            int r = Color.red(p);
            int g = Color.green(p);
            int b = Color.blue(p);

            float[] hsv = new float[3];


            Color.RGBToHSV(r, g, b, hsv);
            hsv[0] = nbr;
            hsv[1] = 1.0f;

            pixels[i] = Color.HSVToColor(hsv);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }


    // II-Contrast


    // function that calculates the histogram of a bitmap given :

    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  @return a tab filled with the numb of pixels with gray level.
     * function that calculates the histogram of a bitmap given */

    public static int[] histogram(Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] hist = new int[256];
        int[] pixels = new int[h * w];

        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int x = 0; x < pixels.length; ++x) {
            int R = Color.red(pixels[x]);
            int G = Color.green(pixels[x]);
            int B = Color.blue(pixels[x]);

            int gray = (int) (0.3 * R + 0.59 * G + 0.11 * B);

            hist[gray] = hist[gray] + 1;
        }

        return hist;

    }

    //linear extention

    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  @return a bitmap.
     *  function that calculates a linear transformation between [min,max] over 256 level of gray*/

    public static Bitmap dynamic_extension(Bitmap bmp) {

        toGray(bmp);
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];


        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        Bitmap last = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        int r_max = 0 ;
        int g_max = 0;
        int b_max = 0 ;

        int r_min = 0 ;
        int g_min = 0;
        int b_min = 0 ;

        int R0 = Color.red(pixels[0]);
        int G0 = Color.green(pixels[0]);
        int B0 = Color.blue(pixels[0]);

        for (int i = 0; i < pixels.length; ++i) {
            if (Color.red(pixels[i])> R0) {
                r_max= Color.red(pixels[i]);
            } else if ( Color.red(pixels[i]) < R0) {
                r_min = Color.red(pixels[i]);
            }


            if (Color.green(pixels[i])> G0) {
                g_max= Color.green(pixels[i]);
            } else if ( Color.green(pixels[i]) < G0) {
                g_min = Color.green(pixels[i]);
            }


            if (Color.blue(pixels[i])> B0) {
                b_max= Color.blue(pixels[i]);
            } else if ( Color.blue(pixels[i]) < B0) {
                b_min = Color.blue(pixels[i]);
            }


        }
        // Applies linear extension of dynamics to the bitmap

        for (int i = 0; i < pixels.length; ++i) {
            int R = 255 * ((Color.red(pixels[i])) - r_min) / (r_max - r_min);
            int G = 255 * ((Color.green(pixels[i])) - g_min) / (g_max - g_min);
            int B = 255 * ((Color.blue(pixels[i])) - b_min) / (b_max - b_min);
            pixels[i] = Color.rgb(R, G, B);
        }

        last.setPixels(pixels, 0, w, 0, 0, w, h);
        return last ;





    }
    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  function that forces the levels of gray to be organized between 0 and 255 */

    public static void histogramEqualization_gray(Bitmap bmp) {


        toGray(bmp); //gray filter
        int w = bmp.getWidth();
        int h = bmp.getHeight();


        int[] pixels = new int[h * w];

        int[] hist = histogram(bmp);

        int[] C = new int[hist.length];
        C[0] = hist[0];
        for (int i = 1; i < hist.length; ++i) {
            C[i] = C[i - 1] + hist[i];  // histogram's sum
        }
        //equalization:
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < pixels.length; ++i) {
            int R = Color.red(pixels[i]);  // transformation of the gray level
            R = C[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);

    }
    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  same thing as histogramEqualization_gray but without graying the bitmap first */


    public static void histogramEqualization_RGB(Bitmap bmp) {


        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];


        int[] hist = histogram(bmp);
        int[] C = new int[hist.length];
        C[0] = hist[0];
        for (int i = 1; i < hist.length; ++i) {
            C[i] = C[i - 1] + hist[i];
        }
        //equalization:
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < pixels.length; ++i) {
            int R = Color.red(pixels[i]);
            R = C[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }


        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }

    /**
     * function that creates noise effect
     * @author https://xjaphx.wordpress.com/2011/10/30/image-processing-flea-noise-effect/
     * @param bmp
     */


    public static void FleaEffect(Bitmap bmp) {
        // get image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor; //add randColor to pixels[index]
            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);

    }

    /**
     * it increases the pixels of a color chosen and with a given percentage
     * @author https://xjaphx.wordpress.com/
     * @param bmp
     * @param type
     * @param percent
     */

    public static void boost(Bitmap bmp, int type, float percent) {
    int width = bmp.getWidth();
    int height = bmp.getHeight();


    int A, R, G, B;
    int pixel;

    for(int x = 0; x < width; ++x) {
        for(int y = 0; y < height; ++y) {
            pixel = bmp.getPixel(x, y);
            A = Color.alpha(pixel);
            R = Color.red(pixel);
            G = Color.green(pixel);
            B = Color.blue(pixel);
            if(type == 1) {
                R = (int)(R * (1 + percent));
                if(R > 255) R = 255;
            }
            else if(type == 2) {
                G = (int)(G * (1 + percent));
                if(G > 255) G = 255;
            }
            else if(type == 3) {
                B = (int)(B * (1 + percent));
                if(B > 255) B = 255;
            }
            bmp.setPixel(x, y, Color.argb(A, R, G, B));
        }
    }
}

    /**
     *
     * @param bmp
     * take a bitmap as a parameter.
     * function that brighten a picture using an histogram for every R,G,B
     */

    public static void over_exposure(Bitmap bmp) {


        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];


        int[] hist_red = new int[256];
        int[] hist_green = new int[256];
        int[] hist_blue = new int[256];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int coeff = 10;

        for (int i = 0; i < pixels.length; ++i) {
            hist_red[(Color.red(pixels[i]) * 255 - coeff )/ 255] += 1;
            hist_green[(Color.green(pixels[i]) * 255 - coeff )/ 255] += 1;
            hist_blue[(Color.blue(pixels[i]) * 255 - coeff) / 255] += 1;
        }

        int[] C_red = new int[hist_red.length];
        int[] C_green = new int[hist_green.length];
        int[] C_blue = new int[hist_blue.length];


        C_red[0] = hist_red[0];
        C_green[0] = hist_green[0];
        C_blue[0] = hist_blue[0];


        for (int i = 1; i < hist_red.length; ++i) {
            C_red[i] = C_red[i - 1] + hist_red[i];
            C_green[i] = C_green[i - 1] + hist_green[i];
            C_blue[i] = C_blue[i - 1] + hist_blue[i];
        }
        //equalization:

        for (int i = 0; i < pixels.length; ++i) {
            int R = Color.red(pixels[i]);
            R = C_red[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C_green[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C_blue[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }

      public static PorterDuffColorFilter setBrightness(int progress) {
        if (progress >= 100) {
            int value = (progress - 100) * 255 / 100;

            return new PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER);

        } else {
            int value =  (100 - progress) * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);


        }
    }

}





