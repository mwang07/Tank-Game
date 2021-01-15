package ImageObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LivesUP extends ImageObject {

    private int x;
    private int y;
    private BufferedImage img;

    public LivesUP(int x, int y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void drawImage(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, x, y, null);
    }

    public Rectangle getRec(){
        return new Rectangle(x, y, img.getWidth(), img.getHeight());
    }
}
