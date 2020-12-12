import java.util.ArrayList;
import java.util.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class Parser {
    public static void main(String[] args) {
        try {
            String baka;
            FileWriter writer0 = new FileWriter("output0.txt", false);
            FileWriter writer1 = new FileWriter("output1.txt", false);
            FileWriter writer2 = new FileWriter("output2.txt", false);
            FileWriter writer3 = new FileWriter("output3.txt", false);
            FileWriter writer4 = new FileWriter("output4.txt", false);
            FileWriter writer5 = new FileWriter("output5.txt", false);
            FileWriter writer6 = new FileWriter("output6.txt", false);

            System.out.print("p");
            BufferedImage bi = ImageIO.read(new File("image.png"));

            int w = bi.getWidth();
            int h = bi.getHeight();
            int[][] pictureR = new int[w][h];
            int[][] pictureG = new int[w][h];
            int[][] pictureB = new int[w][h];
            int[][] yChecker = new int[w][h];


            int x = 0;
            int y = 0;
            System.out.println("pp");
            while (x < w) {
                y = 0;
                while (y < h) {
                    int rgb = bi.getRGB(x, y);
                    pictureR[x][y] = (rgb >> 16) & 0xFF;
                    pictureG[x][y] = (rgb >> 8) & 0xFF;
                    pictureB[x][y] = (rgb) & 0xFF;
                    y++;
                }
                x++;
            }
            System.out.println("ppa");
            x = 0;
            int lastY = 0;
            int counter = 1;
            while (x < w) {
                y = 0;
                lastY = 0;
                while (y < h) {
                    if (lastY != y) {
                        if (bi.getRGB(x, y) == bi.getRGB(x, lastY)) {
                            counter++;
                            yChecker[x][y] = -1;
                        } else {
                            yChecker[x][lastY] = counter;
                            counter = 1;
                            lastY = y;
                        }
                    }

                    y++;
                }
                yChecker[x][lastY] = counter;
                counter = 1;
                x++;
            }
            System.out.println("ppe");
            x = 0;
            int printer = 0;
            while (x < w) {
                y = 0;
                while (y < h) {
                    if (yChecker[x][y] != -1) {
                        printer++;
                        writer1.append("draw color " + pictureR[x][y] + " " + pictureG[x][y] + " " + pictureB[x][y] + " 255 0 0\ndraw rect " + x + " " + y + " 1 " + (yChecker[x][y] + 1) + " 0 0\n");
                        writer0.append(" " + yChecker[x][y] + " ");
                    } else
                        writer0.append(yChecker[x][y] + " ");
                    if (printer > 100) {
                        printer = 0;
                        writer1.append("drawflush display1\n");
                    }
                    y++;
                }
                x++;
                writer0.append("\n");
            }
            writer1.append("drawflush display1");
/*
x=0;
while(x<w){
    y=0;
    while(y<h){
        if(pictureFh1[x][y]!=-1){writer1.append(" "+pictureFh1[x][y]+" ");}else
        writer1.append(pictureFh1[x][y]+" ");
        y++;
    }
    x++;writer1.append("\n");
}

 */
            writer0.flush();
            writer1.flush();
            writer3.flush();
            System.out.println("end");
        } catch (IOException e) {
            System.out.println("err");
        }
        ;

    }
}

