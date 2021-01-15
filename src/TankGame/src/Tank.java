import ImageObject.Bullet;
import ImageObject.ImageObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tank{

    private int x;
    private int y;
    private int vx;
    private int vy;
    private int angle;
    private BufferedImage img;
    private int health;
    private int lives;
    private int power;
    private ArrayList<Bullet> bulletShot = new ArrayList<>();

    private final int R = 2;
    private final int ROTATIONSPEED = 2;

    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean Shoot;

    // 1 forward; 0 backward
    private boolean moveDirection;


    Tank(int x, int y, int vx, int vy, int angle, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.angle = angle;
        this.health = 100;
        this.lives = 2;
        this.power = 20;
    }


    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void toggleShootPressed() {
        this.Shoot = true;
    }

    void unToggleShootPressed() {
        this.Shoot = false;
    }

    long lastPress =0;

    public void update() {

        if (this.UpPressed) {
            this.moveForwards();
            this.moveDirection = true;
        }
        if (this.DownPressed) {
            this.moveBackwards();
            this.moveDirection = false;
        }
        if (this.LeftPressed) {
            this.rotateLeft();
        }
        if (this.RightPressed) {
            this.rotateRight();
        }
        if(this.Shoot){
            if(System.currentTimeMillis() - lastPress > 500) {
                this.shooting();
                //System.out.println(System.currentTimeMillis()+" | "+lastPress);
                lastPress = System.currentTimeMillis();
            }
        }
        //update bullets while updating tanks
        bulletShot.forEach((b) -> b.update());

    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
    }

    private void moveForwards() {
        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
    }


    public void shooting(){
        int bulletX = 0;
        int bulletY = 0;

        if(this.angle%360 >= 270 || this.angle <= 90)
            bulletX = (int) (this.x + img.getWidth()/2 + Math.abs(img.getWidth()/2 * Math.cos(Math.toRadians(angle))));
        else
            bulletX = (int) (this.x + img.getWidth()/2 - Math.abs(img.getWidth()/2 * Math.cos(Math.toRadians(angle))));

        if(this.angle%360 <= 180)
            bulletY = (int) (this.y + img.getHeight()/2 + Math.abs(img.getWidth()/2 * Math.sin(Math.toRadians(angle))));
        else
            bulletY = (int) (this.y + img.getHeight()/2 - Math.abs(img.getWidth()/2 * Math.sin(Math.toRadians(angle))));

        Bullet b = new Bullet(bulletX, bulletY, this.angle, GameWorld.getrBulletImage(), power);
        bulletShot.add(b);
    }

    /**
     * Checking
     */
    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameWorld.MAP_WIDTH - 88) {
            x = GameWorld.MAP_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameWorld.MAP_HEIGHT - 80) {
            y = GameWorld.MAP_HEIGHT - 80;
        }
    }

    public boolean checkAlive(){
        if(this.lives < 0)
            return true;
        else return false;
    }


    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }

    /**
     * Collision
     */
    public void collisionWall(){
        if(moveDirection){
            x -= vx;
            y -= vy;
        }else{
            x += vx;
            y += vy;
        }
    }

    public boolean bulletHitWall(ImageObject w){
        for( int i = 0; i < bulletShot.size(); i++){
            if(bulletShot.get(i).getRec().intersects(w.getRec())){
                bulletShot.remove(bulletShot.get(i));
                return true;
            }
        }
        return false;
    }

    public void bulletHitTank(Tank otherTank){
        for( int i = 0; i < bulletShot.size(); i++){
            if(bulletShot.get(i).getRec().intersects(otherTank.getRec())){
                System.out.print("Before: "+ otherTank.health);
                otherTank.getDamage(bulletShot.get(i).getPower());
                bulletShot.remove(bulletShot.get(i));
                System.out.println(" | After: "+otherTank.health);
            }
        }
    }

    public void collisionTank(){
        if(moveDirection){
            x -= vx;
            y -= vy;
        }else{
            x += vx;
            y += vy;
        }

    }

    /**
     * getters
     *
     */
    public int getX(){
        return x;
    }

    public int getY() {
        return y;
    }

    public ArrayList<Bullet> getBulletShot() {
        return bulletShot;
    }

    public Rectangle getRec(){
        return new Rectangle(x, y, img.getWidth(), img.getHeight());
    }

    //get hit
    public void getDamage(int power){
        if(health - power <= 0){
            lives--;
            health = health - power + 100;
        }else{
            health -= power;
        }
    }


    /**
     * Draw graphics
     */

    void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
    }

    public void drawHealth(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        if(health > 70)
            g.setColor(Color.green);
        else if(health < 30)
            g.setColor(Color.red);
        else
            g.setColor(Color.orange);
        g2d.fillRect(x, y - 10, img.getWidth() * health / 100, 10);
    }

    public void drawLives(Graphics g, BufferedImage bi){
        Graphics2D g2d = (Graphics2D) g;
        int space = 0;
        for(int i = 0; i<lives;i++) {
            g2d.drawImage(bi, this.x + space, this.y, null);
            space += bi.getWidth();
        }
    }

    /**
     * Power Up
     */
    public void powerUp() {
        power += 10;
    }

    public void livesUp(){
        lives++;
    }
}
