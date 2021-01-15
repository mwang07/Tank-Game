package ImageObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends ImageObject {
    private int x;
    private int y;
    private BufferedImage img;
    private boolean breakable;
    private boolean broken;


    public Wall(int x, int y, boolean breakable, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.breakable = breakable;
        this.img = img;
        this.broken = false;
    }

    public void update() {

    }

    public boolean isBreakable() {
        return breakable;
    }

    public boolean isBroken() {
        return broken;
    }

    @Override
    public void drawImage(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, x, y, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getRec(){
        return new Rectangle(x, y, img.getWidth(), img.getHeight());
    }
}
