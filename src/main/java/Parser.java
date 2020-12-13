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

            FileWriter writer = new FileWriter("schematic.txt", false);

            ArrayList<Rect> rects = new ArrayList<>();
            Set<Integer> colors = new HashSet<Integer>();

            System.out.print("Starting load picture");
            BufferedImage bi = ImageIO.read(new File("image.png"));

            int w = bi.getWidth();
            if (w > 176) {
                w = 176;
            }
            int h = bi.getHeight();
            if (h > 176) {
                h = 176;
            }
            int[][] pictureR = new int[w][h];
            int[][] pictureG = new int[w][h];
            int[][] pictureB = new int[w][h];
            int[][] yChecker = new int[w][h];


            int x = 0;
            int y = 0;
            System.out.println("picture loading succsess");
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
            System.out.println("picture decoding to base colors succsess");
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
            System.out.println("graphic primetives finding succsess");


            String[][] sch = new String[8][8];
            sch[0][0] = "";
            int maxColor = getBGcolor(rects, colors);
            rects = rmColor(maxColor, rects);
            int schx = 0;
            int schy = 0;
            int stringChecker = 2;
            int PFChecker = 1;
            String TColor = null;
            //FileWriter writerC = new FileWriter("COut"+PFChecker+".txt", false);
            //writerC.append("draw clear " + ((maxColor >> 16) & 0xFF) + " " + ((maxColor >> 8) & 0xFF) + " " + ((maxColor) & 0xFF) + " 255 0 0\n");
            sch[schx][schy] += "draw clear " + ((maxColor >> 16) & 0xFF) + " " + ((maxColor >> 8) & 0xFF) + " " + ((maxColor) & 0xFF) + " 255 0 0\n";
            for (int c : colors) {
                TColor = ("draw color " + ((c >> 16) & 0xFF) + " " + ((c >> 8) & 0xFF) + " " + ((c) & 0xFF) + " 255 0 0\n");
                stringChecker++;
                //writerC.append(TColor);
                sch[schx][schy] += TColor;
                for (Rect r : getColor(c, rects)) {
                    sch[schx][schy] += "draw rect " + r.x + " " + r.y + " 1 " + r.z + " 0 0\n";
                    stringChecker++;
                    if (stringChecker == 250 || stringChecker == 500 || stringChecker == 750) {
                        stringChecker++;
                        sch[schx][schy] += "drawflush display1\n";
                    }
                    if (stringChecker == 999 && schx == 0 && schy == 0) {
                        stringChecker = 2;
                        sch[schx][schy] += "drawflush display1\nend";
                        PFChecker++;
                        schx++;
                        if (schx > 7) {
                            schy++;
                            schx = 0;
                        }
                        sch[schx][schy] = "";
                        sch[schx][schy] += TColor;
                    }
                    if (stringChecker == 1000 || stringChecker > 999) {
                        stringChecker = 2;
                        sch[schx][schy] += "drawflush display1";
                        PFChecker++;
                        schx++;
                        if (schx > 7) {
                            schy++;
                            schx = 0;
                        }
                        sch[schx][schy] = "";
                        sch[schx][schy] += TColor;
                    }
                }
            }

            writer.append(generate(sch));
            writer.flush();

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

