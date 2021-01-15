package ImageObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends ImageObject{

    private int x;
    private int y;
    private int angle;
    private int speed;
    private BufferedImage img;
    private int power;

    public Bullet(int x, int y, int angle, BufferedImage img, int power) {
        this.x = x - img.getWidth()/2;
        this.y = y - img.getHeight()/2;
        this.angle = angle;
        this.img = img;
        this.power = power;
        speed = 4;
    }

    public void update() {
        x += (int) Math.round(speed * Math.cos(Math.toRadians(angle)));;
        y += (int) Math.round(speed * Math.sin(Math.toRadians(angle)));
    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth(), this.img.getHeight());
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
    }

    public Rectangle getRec(){
        return new Rectangle(x, y, img.getWidth(), img.getHeight());
    }

    public int getPower() {
        return power;
    }
}
