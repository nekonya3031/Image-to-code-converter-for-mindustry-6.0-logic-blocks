import java.util.ArrayList;
import java.util.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class Parser{
    public static void main(String[] args) {
    try{
    String baka;
    FileWriter writer0 = new FileWriter("output0.txt", false);
    FileWriter writer1 = new FileWriter("output1.txt", false);
    FileWriter writer2 = new FileWriter("output2.txt", false);
    FileWriter writer3 = new FileWriter("output3.txt", false);
    FileWriter writer4 = new FileWriter("output4.txt", false);
    FileWriter writer5 = new FileWriter("output5.txt", false);
    FileWriter writer6 = new FileWriter("output6.txt", false);
    
    
    BufferedImage bi = ImageIO.read( new File( "image.png" ) );
    int w=bi.getWidth();
    int h=bi.getHeight();
    int[][] pictureR=new int[w][h];
    int[][] pictureG=new int[w][h];
    int[][] pictureB=new int[w][h];
    int counter=0;
    
    writer0.write("\r");writer2.write("\r");writer1.write("\r");
    int x=0;int y=0;
while(x<w){
y=0;
while(y<h){
int rgb=bi.getRGB(x,y);
pictureR[x][y] = (rgb >> 16) & 0xFF; 
pictureG[x][y] = (rgb >> 8) & 0xFF; 
pictureB[x][y] = (rgb ) & 0xFF;
baka="draw rect "+x+" "+(h-y)+" 1 1 0\n";
if(counter<1001){
writer0.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer0.append(baka);
baka="drawflush @0\n";
writer0.append(baka);}

if(counter>1000&&counter<2001){
writer1.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer1.append(baka);
baka="drawflush @0\n";
writer1.append(baka);}

if(counter>2000&&counter<3001){
writer2.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer2.append(baka);
baka="drawflush @0\n";
writer2.append(baka);}

if(counter<4001&&counter>3000){
writer3.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer3.append(baka);
baka="drawflush @0\n";
writer3.append(baka);}

if(counter>4000&&counter<5001){
writer4.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer4.append(baka);
baka="drawflush @0\n";
writer4.append(baka);}

if(counter>5000&&counter<6001){
writer5.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer5.append(baka);
baka="drawflush @0\n";
writer5.append(baka);}
if(counter>6000){
writer6.append(baka);
baka="draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 0 0\n";
writer6.append(baka);
baka="drawflush @0\n";
writer6.append(baka);}
counter++;
y++;
}
x++;
}
writer0.flush();
writer1.flush();
writer2.flush();
if(counter<2134){System.out.println("1");}
if(counter>2133&&counter<4267){System.out.println("2");}
if(counter>4266){System.out.println("3");}
}catch(IOException e){};


}}
