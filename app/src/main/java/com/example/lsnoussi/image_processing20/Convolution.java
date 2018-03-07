package com.example.lsnoussi.image_processing20;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by lsnoussi on 07/03/18.
 */

public class Convolution {

    /**
     * take a bitmap as a parameter.
     *  function to blur using kernel matrix
     * @param bmp
     */

    public static void Moyenneur(Bitmap bmp) {

        int[][] Matrix = new int[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Matrix[i][j] = 1;
            }
        }

        int width = bmp.getWidth();
        int height = bmp.getHeight();



        int sumR, sumG, sumB = 0;
        int[] pixels = new int [width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);



        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {

                sumR = sumG = sumB = 0;

                int index=0;

                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {
                        index = (y+v)*width +(x+u);
                        sumR += Color.red(pixels[index]) * Matrix[u + 1][v + 1];
                        sumG += Color.green(pixels[index]) * Matrix[u + 1][v + 1];
                        sumB += Color.blue(pixels[index]) * Matrix[u + 1][v + 1];
                    }
                }

                sumR = sumR / 9;

                sumG = sumG / 9;

                sumB = sumB / 9;

                pixels[index] =  Color.rgb(sumR, sumG, sumB);

            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);



    }

       /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  function to blur using gauss matrix */


    public static void Gauss_convolution(Bitmap bmp) {


        int[][] Matrix = new int[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        int width = bmp.getWidth();
        int height = bmp.getHeight();


        int sumR, sumG, sumB = 0;

        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);


        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {

                sumR = sumG = sumB = 0;
                int index = 0;


                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {

                        index = (y + v) * width + (x + u);
                        sumR += Color.red(pixels[index]) * Matrix[u + 1][v + 1];
                        sumG += Color.green(pixels[index]) * Matrix[u + 1][v + 1];
                        sumB += Color.blue(pixels[index]) * Matrix[u + 1][v + 1];
                    }
                }


                sumR = sumR / 16;

                sumG = sumG / 16;

                sumB = sumB / 16;


                pixels[index] = Color.rgb(sumR, sumG, sumB);

            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);

    }


}
