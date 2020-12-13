import arc.struct.Seq;
import arc.struct.StringMap;
import mindustry.Vars;
import mindustry.core.ContentLoader;
import mindustry.core.Version;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.world.Block;

import java.util.ArrayList;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.zip.DeflaterOutputStream;

//todo фикс у коорды. фикс зависимостей
public class Parser {
    public static void main(String[] args) {
        Version.enabled = false;
        Vars.content = new ContentLoader();
        Vars.content.createBaseContent();
        for (ContentType type : ContentType.values()) {
            for (Content content : Vars.content.getBy(type)) {
                try {
                    content.init();
                } catch (Throwable ignored) {
                }
            }
        }
        try {
            String baka;
            FileWriter writer0 = new FileWriter("output0.txt", false);
            FileWriter writer1 = new FileWriter("output1.txt", false);
            FileWriter writer2 = new FileWriter("output2.txt", false);
            FileWriter writer3 = new FileWriter("output3.txt", false);
            FileWriter writer4 = new FileWriter("output4.txt", false);
            FileWriter writer5 = new FileWriter("output5.txt", false);

            ArrayList<Rect> rects = new ArrayList<>();
            Set<Integer>colors=new HashSet<Integer>();

            System.out.print("p");
            BufferedImage bi = ImageIO.read(new File("image.png"));

            int w = bi.getWidth();
            if(w>176){w=176;}
            int h = bi.getHeight();
            if(h>176){h=176;}
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
                    colors.add(rgb);
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
                        rects.add(new Rect(x,y,yChecker[x][y] + 1,bi.getRGB(x, y)));
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
            System.out.println("end standart actions set");

            String[][] sch=new String[8][8];
            sch[0][0] = "";
            int maxColor=getBGcolor(rects,colors);
            rects=rmColor(maxColor,rects);
            int schx=0;
            int schy=0;
            int stringChecker=2;
            int PFChecker=1;
            String TColor=null;
            FileWriter writerC = new FileWriter("COut"+PFChecker+".txt", false);
            writerC.append("draw clear " + ((maxColor >> 16) & 0xFF) + " " + ((maxColor >> 8) & 0xFF) + " " + ((maxColor) & 0xFF) + " 255 0 0\n");
            sch[schx][schy]+="draw clear " + ((maxColor >> 16) & 0xFF) + " " + ((maxColor >> 8) & 0xFF) + " " + ((maxColor) & 0xFF) + " 255 0 0\n";
            for(int c:colors){
                TColor=("draw color " + ((c >> 16) & 0xFF) + " " + ((c >> 8) & 0xFF) + " " + ((c) & 0xFF) + " 255 0 0\n");
                stringChecker++;
                writerC.append(TColor);
                sch[schx][schy]+=TColor;
                for(Rect r:getColor(c,rects)){
                    writerC.append("draw rect " + r.x + " " + r.y + " 1 " + r.z + " 0 0\n");
                    sch[schx][schy]+="draw rect " + r.x + " " + r.y + " 1 " + r.z + " 0 0\n";
                    stringChecker++;
                    if(stringChecker==250||stringChecker==500||stringChecker==750){
                        writerC.append("drawflush display1\n");stringChecker++;
                        sch[schx][schy]+="drawflush display1\n";
                    }
                    if(stringChecker==1000||stringChecker>999){
                        writerC.append("drawflush display1");stringChecker=2;
                        sch[schx][schy]+="drawflush display1";
                        PFChecker++;
                        schx++;
                        if(schx>7){schy++;schx=0;}
                        //TODO killer remove
                        //if(PFChecker>10){return;}
                        writerC.flush();
                        writerC = new FileWriter("COut" + PFChecker + ".txt", false);
                        writerC.append(TColor);
                        sch[schx][schy] = "";
                        sch[schx][schy] += TColor;
                    }
                }
            }
            writerC.flush();
            writer5.append(generate(sch));
            writer5.flush();

            System.out.println("end custom actions set");

        } catch (IOException e) {
            System.out.println("err");
        }
        ;

    }

    public static ArrayList<Rect> getColor(int c, ArrayList<Rect> a){
        ArrayList<Rect> rtn=new ArrayList<>();
        for(Rect r:a){
            if(r.rgb==c){
                rtn.add(r);
            }}
        return rtn;
    }

    public static ArrayList<Rect> rmColor(int c, ArrayList<Rect> a){
        ArrayList<Rect> rtn=new ArrayList<>();
        for(Rect r:a){
            if(r.rgb!=c){
                rtn.add(r);
            }}
        return rtn;
    }

    public static Integer getBGcolor(ArrayList<Rect> a, Set<Integer> c){
        int rtn=0;
        int maxWal=0;
        int tempWal=0;
        for(int e:c){
            tempWal = 0;
            for (Rect r : a) {
                if (r.rgb == e) {
                    tempWal++;
                }
            }
            if (tempWal > maxWal) {
                maxWal = tempWal;
                rtn = e;
            }
        }
        return rtn;
    }

    public static String generate(String[][] c) {
        String sirterSch = "bXNjaAF4nGNgZGBkYmDJS8xNZeC4sP9iw4V9F9sYuFNSi5OLMgtKMvPzGBgYGfhzM5OL8nULivKTU4uL84sYQIIgwAfE3BVzklMamBgYhBiYGADInhTo";
        Block lp = null;
        Schematic sorterschema = Schematics.readBase64(sirterSch);
        for (Schematic.Stile gg : sorterschema.tiles) {
            lp = gg.block;
        }
        if (lp == null) {
            System.out.println("ERROR SYKA");
        }
        int a = 0;
        int b;
        byte f = 0;
        Seq<Schematic.Stile> tiles = new Seq<>();
        while (a < 8) {
            b = 0;
            while (b < 7) {
                if (c[a][b] != null) {
                    tiles.add(new Schematic.Stile(lp, a, b, compress(c[a][b]), f));
                }
                b++;
            }
            a++;
        }
        StringMap tags = new StringMap();
        tags.put("name", "photo");
        Schematic schem = new Schematic(tiles, tags, 8, 8);

        Schematics aaa = new Schematics();
        aaa.saveChanges(schem);
        return aaa.writeBase64(schem);
    }

    static byte[] compress(String code) {
        return compress(code.getBytes(Vars.charset));
    }

    static byte[] compress(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(baos));
            stream.write(1);
            stream.writeInt(bytes.length);
            stream.write(bytes);
            int actives = 0;
            stream.writeInt(actives);
            stream.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Rect {
    int x;
    int y;
    int z;
    int rgb;

    Rect(int x, int y, int z, int rgb) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rgb=rgb;
    }
}

