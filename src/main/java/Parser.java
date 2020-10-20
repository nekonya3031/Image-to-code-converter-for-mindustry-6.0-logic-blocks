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
    
    System.out.print("p");
    BufferedImage bi = ImageIO.read( new File( "image.png" ) );
    
    int w=bi.getWidth();
    int h=bi.getHeight();
    int[][] pictureR=new int[w][h];
    int[][] pictureG=new int[w][h];
    int[][] pictureB=new int[w][h];
    int[][] pictureFw1=new int[w][h];
    int[][] pictureFh1=new int[w][h];
    int[][] pictureFw=new int[w][h];
    int[][] pictureFh=new int[w][h];
    int[][] pictureOu=new int[w][h];
    int counter=0;
    
    int x=0;int y=0;
    System.out.println("pp");
while(x<w){
y=0;
while(y<h){
int rgb=bi.getRGB(x,y);
pictureR[x][y] = (rgb >> 16) & 0xFF; 
pictureG[x][y] = (rgb >> 8) & 0xFF; 
pictureB[x][y] = (rgb ) & 0xFF;
pictureFh[x][y]=rgb;
pictureFw[x][y]=rgb;
y++;
}
x++;
}
System.out.println("ppa");
x=0;
while(x<w){
    y=0;
    while(y<h){
    if(pictureFh1[x][y]!=-1){
        int e=1;
        while((x+e<w)&&pictureFh[x][y]==pictureFh[x+e][y]){pictureFh1[x+e][y]=-1;e++;}
        if((x+1<w)&&pictureFh[x][y]!=pictureFh[x+1][y]){pictureFh[x][y]=0;}else{pictureFh1[x][y]=e-1;}}
        y++;
    }
    x++;
}
System.out.println("ppe");
x=0;
while(x<w){
    y=0;
    while(y<h){
    if(pictureFw1[x][y]!=-1){
        int e=1;
        while((y+e<h)&&pictureFw[x][y]==pictureFw[x][y+e]){pictureFw1[x][y+e]=-1;e++;}
        if((y+1<h)&&pictureFw[x][y]!=pictureFw[x][y+1]){pictureFw[x][y]=0;}else{pictureFw1[x][y]=e-1;}}
        y++;
    }
    x++;
}
x=0;
System.out.println("pps");
while(x<w){
    y=0;
    while(y<h){
        if(pictureFh1[x][y]!=-1){ writer3.append("draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 255 0 0\ndraw rect "+x+" "+(h-y)+" "+(pictureFh1[x][y]+1)+" 1 0 0\n");writer0.append(" "+pictureFh1[x][y]+" ");}else
        writer0.append(pictureFh1[x][y]+" ");
       
        y++;
    }
    x++;writer0.append("\n");
}
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
writer0.flush();writer1.flush();
System.out.println("end");
}catch(IOException e){System.out.println("err");};

}}

