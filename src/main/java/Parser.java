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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.zip.DeflaterOutputStream;

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
            FileWriter writer0 = new FileWriter("schematicCode.txt", false);
            System.out.print("p");
            BufferedImage bi = ImageIO.read(new File("image.png"));
            AffineTransform at = AffineTransform.getScaleInstance(1, -1);
            at.translate(0, -bi.getHeight());
            AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            bi = op.filter(bi, null);

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
            String[][] commands = new String[8][8];
            commands[0][0] = "";
            int arrX = 0;
            int arrY = 0;
            x = 0;
            int printer = 0;
            while (x < w) {
                y = 0;
                while (y < h) {
                    if (yChecker[x][y] != -1) {
                        printer++;
                        commands[arrX][arrY] += ("draw color " + pictureR[x][y] + " " + pictureG[x][y] + " " + pictureB[x][y] + " 255 0 0\ndraw rect " + x + " " + y + " 1 " + (yChecker[x][y] + 1) + " 0 0\n");
                    }
                    if (printer == 125 || printer == 250 || printer == 375) {
                        commands[arrX][arrY] += ("drawflush display1\n");
                    }
                    if (printer > 490) {
                        printer = 0;
                        commands[arrX][arrY] += ("drawflush display1\n");

                        arrX++;
                        if (arrX > 7) {
                            arrX = 0;
                            arrY++;
                        }
                        commands[arrX][arrY] = "";
                    }
                    y++;
                }
                x++;

            }
            writer0.append(generate(commands));
            writer0.flush();
            System.out.println("end");
        } catch (IOException e) {
            System.out.println("err");
        }
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


