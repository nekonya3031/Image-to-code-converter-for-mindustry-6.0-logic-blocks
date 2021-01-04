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
import mindustry.world.blocks.logic.LogicBlock;

import java.awt.*;
import java.util.ArrayList;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.zip.DeflaterOutputStream;

//todo фикс у коорды. фикс зависимостей
public class Parser {
    public static void main(String[] args) throws IOException {
        System.out.print("Starting load picture");
        BufferedImage bi = ImageIO.read(new File("image.png"));
        parse(bi);
    }

    public static void parse(BufferedImage bi) {
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
            FileWriter writer = new FileWriter("schematic.txt", false);

            ArrayList<Rect> rects = new ArrayList<>();
            Set<Integer> colors = new HashSet<Integer>();


            int w = bi.getWidth();
            if (w > 176) {
                w = 176;
            }
            int h = bi.getHeight();
            if (h > 176) {
                h = 176;
            }


            int[][] yChecker = new int[w][h];
            int[][] rgb = new int[w][h];


            int x = 0;
            int y = 0;
            System.out.println("picture loading succsess");
            while (x < w) {
                y = 0;
                while (y < h) {
                    int baka = bi.getRGB(x, y);
                    int r = (int) ((baka >> 16) & 0xFF) / 12;
                    int g = (int) ((baka >> 8) & 0xFF) / 12;
                    int b = (int) ((baka) & 0xFF) / 12;
                    baka = new Color(r * 12, g * 12, b * 12).getRGB();
                    rgb[x][h - 1 - y] = baka;
                    colors.add(baka);
                    y++;
                }
                x++;
            }
            System.out.println("picture decoding to base colors succsess\nfounded colors: " + colors.size());
            x = 0;
            int lastY = 0;
            int counter = 1;
            while (x < w) {
                y = 0;
                lastY = 0;
                while (y < h) {
                    if (lastY != y) {
                        if (rgb[x][y] == rgb[x][lastY]) {
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
            System.out.println();
            x = 0;
            while (x < w) {
                y = 0;
                while (y < h) {
                    if (yChecker[x][y] != -1) {
                        rects.add(new Rect(x, y, yChecker[x][y] + 1, rgb[x][y]));
                    }
                    y++;
                }
                x++;
            }
            System.out.println("graphic primetives finding succsess");
            System.out.println(rects.size());
            BufferedImage img = new BufferedImage(176, 176, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            for (Rect r : rects) {
                g.setColor(new Color(r.rgb));
                g.fillRect(r.x, r.y, 1, r.z);
            }
            File f = new File("baka.png");
            f.createNewFile();
            ImageIO.write(img, "png", f);
            int processorId = 0;
            String[][] sch = new String[8][8];
            sch[0][0] = "";
            int generatedStrings = 0;
            //int maxColor = getBGcolor(rects, colors);
            //rects = rmColor(maxColor, rects);
            int schx = 0;
            int schy = 0;
            int stringChecker = 2;
            String TColor;
            int id = 0;
            //sch[schx][schy] += "draw clear " + ((maxColor >> 16) & 0xFF) + " " + ((maxColor >> 8) & 0xFF) + " " + ((maxColor) & 0xFF) + " 255 0 0\n";
            stringChecker++;
            stringChecker++;
            sch[0][0] += "read id cell1 1\njump 0 notEqual id " + id + "\n";
            for (int c : colors) {
                TColor = ("draw color " + (int) ((c >> 16) & 0xFF) + " " + (int) ((c >> 8) & 0xFF) + " " + (int) ((c) & 0xFF) + " 255 0 0\n");
                stringChecker++;
                sch[schx][schy] += TColor;

                for (Rect r : getColor(c, rects)) {
                    sch[schx][schy] += "draw rect " + r.x + " " + r.y + " 1 " + r.z + " 0 0\n";
                    stringChecker++;
                    generatedStrings++;
                    if (stringChecker == 250 || stringChecker == 500 || stringChecker == 750) {
                        stringChecker++;
                        sch[schx][schy] += "drawflush display1\n";
                    }
                    if (stringChecker > 996) {
                        stringChecker = 3;
                        id++;
                        sch[schx][schy] += "drawflush display1\nwrite " + id + " cell1 1";
                        schx++;
                        if (schx > 7) {
                            schy++;
                            schx = 0;
                        }
                        sch[schx][schy] = "read id cell1 1\njump 0 notEqual id " + id + "\n";
                        sch[schx][schy] += TColor;
                    }
                }
            }
            sch[schx][schy] += "drawflush display1";
            sch[schx][schy] += "drawflush display1\nwrite 0 cell1 1";
            if (schx == 7) {
                schy++;
                schx = 0;
            } else {
                schx++;
            }
            writer.append(generate(sch, schx, schy));
            writer.flush();
            System.out.println("writed primitives: " + generatedStrings);
            System.out.println("end custom actions set");
        } catch (IOException e) {
            System.out.println("err");
        }


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

    public static String generate(String[][] c, int x, int y) {
        String sorterSch = "bXNjaAF4nGNgZGBkYmDJS8xNZeC4sP9iw4V9F9sYuFNSi5OLMgtKMvPzGBgYGfhzM5OL8nULivKTU4uL84sYQIIgwAfE3BVzklMamBgYhBiYGADInhTo";
        String cellSch = "bXNjaAF4nGNgZGBkYmDJS8xNZeC42HFhx4XtFzYwcKekFicXZRaUZObnMTAwMnDnpubmF1XqJqfm5DCABCAAAEaXELw=";
        String displaySch = "bXNjaAF4nGNgY2BjYmDJS8xNZWBPySwuyEmsZOBOSS1OLsosKMnMz2NgYGQQzkksSk/VzclPz0zWhSkCSTAwASEDAF42EOI=";
        Block lp = null;
        Block cl = null;
        Block ld = null;
        Schematic sorterschema = Schematics.readBase64(sorterSch);
        Schematic cellSchema = Schematics.readBase64(cellSch);
        Schematic displaySchema = Schematics.readBase64(displaySch);
        for (Schematic.Stile gg : sorterschema.tiles) {
            lp = gg.block;
        }
        if (lp == null) {
            System.out.println("ERROR SYKA");
        }
        for (Schematic.Stile gg : cellSchema.tiles) {
            cl = gg.block;
        }
        if (cl == null) {
            System.out.println("ERROR SYKA");
        }
        for (Schematic.Stile gg : displaySchema.tiles) {
            ld = gg.block;
        }
        if (ld == null) {
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
                    Seq<LogicBlock.LogicLink> links = new Seq<>();
                    links.add(new LogicBlock.LogicLink(dx(x, a), dy(y, b), "cell1", true));
                    if (x == 0) {
                        links.add(new LogicBlock.LogicLink(dx(3, a), dy(y + 2, b), "display1", false));
                    } else {
                        links.add(new LogicBlock.LogicLink(dx(3, a), dy(y + 3, b), "display1", false));
                    }
                    tiles.add(new Schematic.Stile(lp, a, b, compress(c[a][b], links), f));
                }
                b++;
            }
            a++;
        }
        tiles.add(new Schematic.Stile(cl, x, y, null, f));
        if (x == 0) {
            tiles.add(new Schematic.Stile(ld, 3, y + 2, null, f));
        } else {
            tiles.add(new Schematic.Stile(ld, 3, y + 3, null, f));
        }

        StringMap tags = new StringMap();
        tags.put("name", "photo");
        Schematic schem = new Schematic(tiles, tags, 8, 8);

        Schematics aaa = new Schematics();
        aaa.saveChanges(schem);
        return aaa.writeBase64(schem);
    }

    static byte[] compress(String code, Seq<LogicBlock.LogicLink> links) {
        return compress(code.getBytes(Vars.charset), links);
    }

    static byte[] compress(byte[] bytes, Seq<LogicBlock.LogicLink> links) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(baos));
            stream.write(1);
            stream.writeInt(bytes.length);
            stream.write(bytes);
            int actives = links.count(l -> l.active);
            stream.writeInt(actives);
            for (LogicBlock.LogicLink link : links) {
                if (!link.active)
                    continue;
                stream.writeUTF(link.name);
                stream.writeShort(link.x);
                stream.writeShort(link.y);
            }
            stream.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static int dx(int x1, int x2) {
        return (x1 - x2);
    }

    static int dy(int y1, int y2) {
        return (y1 - y2);
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

