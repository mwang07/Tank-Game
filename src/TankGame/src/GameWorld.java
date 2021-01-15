import ImageObject.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

import static javax.imageio.ImageIO.read;


public class GameWorld extends JPanel  {


    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 960;
    public static final int MAP_WIDTH = 1920;
    public static final int MAP_HEIGHT = 1600;
    private static BufferedImage rBullet;
    private BufferedImage world;
    private Graphics2D buffer;
    private JFrame jf;
    private Tank t1, t2;
    private BufferedImage background, breakWall, unbreakWall, heart_pickUP, swordUp_pickUP, life;
    private int[] wallMap = new int[3000];
    ArrayList<ImageObject> mapObject = new ArrayList<ImageObject>();

    private BufferedImage tank1View = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
    private BufferedImage tank2View = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
    private BufferedImage miniMap = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);

    public static void main(String[] args) {
        Thread x;
        GameWorld gw = new GameWorld();
        gw.init();
        try {
            while (true) {
                gw.t1.update();
                gw.t2.update();
                gw.collision();
                gw.repaint();
                //System.out.println("T1: " + gw.t1);
                //System.out.println("T2: " + gw.t2);
                if(gw.t1.checkAlive()) {
                    System.out.println("PLAYER 2 WIN!!!");
                    break;
                }
                if(gw.t2.checkAlive()) {
                    System.out.println("PLAYER 1 WIN!!!");
                    break;
                }
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {

        }

    }


    private void init() {
        this.jf = new JFrame("Tank Game");
        this.world = new BufferedImage(GameWorld.MAP_WIDTH, GameWorld.MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
        BufferedImage t1img = null,t2img = null, bg = null, bWall = null, uWall = null, rocket = null,
                heart = null, sword = null;
        try {
            BufferedImage tmp;
            System.out.println(System.getProperty("user.dir"));

            //tank images
            t1img = read(new File("..\\..\\resources\\Tank1.png"));
            t2img = read(new File("..\\..\\resources\\Tank2.png"));
            //background image
            bg = read(new File("..\\..\\resources\\Background.bmp"));
            background = resize(bg, 32, 32);

            //wall
            bWall = read(new File("..\\..\\resources\\Wall1.gif"));
            breakWall = resize(bWall, 32, 32);
            uWall = read(new File("..\\..\\resources\\Wall2.gif"));
            unbreakWall = resize(uWall, 32, 32);

            //bullet
            rocket = read(new File("..\\..\\resources\\rocket.png"));
            rBullet = resize(rocket, 16, 16);

            //power up
            heart = read(new File("..\\..\\resources\\heart.png"));
            heart_pickUP = resize(heart, 32, 32);
            sword = read(new File("..\\..\\resources\\sword_up.png"));
            swordUp_pickUP = resize(sword, 32, 32);

            //life
            life = resize(heart, 8, 8);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        t1 = new Tank(200, 200, 0, 0, 0, t1img);
        t2 = new Tank(MAP_WIDTH - 200, MAP_HEIGHT - 200, 0, 0, 180, t2img);

        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        this.jf.setLayout(new BorderLayout());
        this.jf.add(this);

        this.jf.addKeyListener(tc1);
        this.jf.addKeyListener(tc2);

        this.jf.setSize(GameWorld.SCREEN_WIDTH, GameWorld.SCREEN_HEIGHT + 30);
        this.jf.setResizable(false);
        jf.setLocationRelativeTo(null);

        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jf.setVisible(true);

        //load map
        loadMap();

        //Create background
        addBackGround();

        //create walls
        addWall();

    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    // 0 =empty, 1 = breakable wall, 2 = unbreakable wall, 3 = sword up(damage up), 4 = heart(life + 1)
    public void loadMap() {
        try {
            File file = new File("..\\..\\resources\\walls.txt");
            Scanner sc = new Scanner(file);
            int x = 0;
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                for(int i = 0; i < line.length(); i++) {
                    wallMap[x] = Character.getNumericValue(line.charAt(i));
                    x++;
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot Load in map");
        }
    }

    public void addBackGround() {

        for(int i = 0; i < MAP_WIDTH; i+=MAP_WIDTH/60){       //32
            for(int j = 0; j < MAP_HEIGHT; j+=MAP_HEIGHT/50) { //32
                ImageObject io = new ImageObject(i, j, background);
                mapObject.add(io);
            }
        }
    }


    public void addWall(){
        for(int j = 0; j <50;j++) {
            for (int i = j * 60; i < j * 60 + 60; i++) {
                //add breakable wall
                if (wallMap[i] == 1) {
                    Wall w = new Wall((i % 60) * 32, j * 32, false, unbreakWall);
                    mapObject.add(w);
                }
                //add unbreakable wall
                if (wallMap[i] == 2) {
                    Wall w = new Wall((i % 60) * 32, j * 32, true, breakWall);
                    mapObject.add(w);
                }
                //power up
                if (wallMap[i] == 3) {
                    SwordUP su = new SwordUP((i % 60) * 32, j * 32,  swordUp_pickUP);
                    mapObject.add(su);
                }
                if (wallMap[i] == 4) {
                    LivesUP lu = new LivesUP((i % 60) * 32, j * 32,  heart_pickUP);
                    mapObject.add(lu);
                }
            }
        }
    }



    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        buffer = world.createGraphics();
        super.paintComponent(g2);


        for(int i = 0; i < mapObject.size(); i++)
            mapObject.get(i).drawImage(buffer);
        //draw tank
        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);

        //draw health bar
        this.t1.drawHealth(buffer);
        this.t2.drawHealth(buffer);

        //draw lives
        this.t1.drawLives(buffer, life);
        this.t2.drawLives(buffer, life);

        //bullets t1 and t2
        ArrayList<Bullet> b1 = this.t1.getBulletShot();
        b1.forEach((b) -> b.drawImage(buffer));
        ArrayList<Bullet> b2 = this.t2.getBulletShot();
        b2.forEach((b) -> b.drawImage(buffer));

        g2.drawImage(world,0,0,null);

        tank1View = world.getSubimage(currentPositionX(t1), currentPositionY(t1), SCREEN_WIDTH/2, SCREEN_HEIGHT);
        tank2View = world.getSubimage(currentPositionX(t2), currentPositionY(t2), SCREEN_WIDTH/2, SCREEN_HEIGHT);
        miniMap = world.getSubimage(0, 0, MAP_WIDTH, MAP_HEIGHT);

        //scale minimap
        BufferedImage temp = new BufferedImage(miniMap.getWidth(), miniMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(0.2, 0.2);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        miniMap = scaleOp.filter(miniMap, temp);

        //System.out.println("T1: "+currentPositionX(t1)+" | "+currentPositionY(t2));
        //System.out.println("T1: "+currentPositionX(t2)+" | "+currentPositionY(t2));
        g2.drawImage(tank1View,0,0,this);
        g2.drawImage(tank2View,SCREEN_WIDTH/2,0,this);
        g2.drawImage(miniMap,SCREEN_WIDTH/2 - MAP_WIDTH/10, SCREEN_HEIGHT - MAP_HEIGHT/5,this);
    }

    public int currentPositionX(Tank t) {
        int x;
        x = t.getX() - SCREEN_WIDTH/4;

        if (x < 0) {
            x = 0;
        }else if (x > (SCREEN_WIDTH)) {
            x = (SCREEN_WIDTH);
        }
        return x;
    }

    public int currentPositionY(Tank t) {
        int y;
        y = t.getY()- SCREEN_HEIGHT/2;

        if (y < 0) {
            y = 0;
        }else if (y > (MAP_HEIGHT - SCREEN_HEIGHT)) {
            y = (MAP_HEIGHT - SCREEN_HEIGHT);
        }
        return y;
    }

    public static BufferedImage getrBulletImage()
    {
        return rBullet;
    }

    public void collision(){
        //tank interact with other tank
        if (t1.getRec().intersects(t2.getRec())) {
            t1.collisionTank();
            t2.collisionTank();
        }

        //looping wall objects
        for(int i = 0; i < mapObject.size(); i++){
            if(mapObject.get(i) instanceof Wall){
                //wall interact with tanks
                if(t1.getRec().intersects(mapObject.get(i).getRec()))
                    t1.collisionWall();
                if(t2.getRec().intersects(mapObject.get(i).getRec()))
                    t2.collisionWall();

                //bullets interact with wall
                if(t1.bulletHitWall(mapObject.get(i))){
                    if(((Wall) mapObject.get(i)).isBreakable())
                        mapObject.remove(mapObject.get(i));
                }
                if(t2.bulletHitWall(mapObject.get(i))){
                    if(((Wall) mapObject.get(i)).isBreakable())
                        mapObject.remove(mapObject.get(i));
                }
            }

            //Power Up
            if(mapObject.get(i) instanceof SwordUP){
                if(t1.getRec().intersects(mapObject.get(i).getRec())) {
                    t1.powerUp();
                    mapObject.remove(mapObject.get(i));
                }
                if(t2.getRec().intersects(mapObject.get(i).getRec())) {
                    t2.powerUp();
                    mapObject.remove(mapObject.get(i));
                }
            }

            if(mapObject.get(i) instanceof LivesUP){
                if(t1.getRec().intersects(mapObject.get(i).getRec())) {
                    t1.livesUp();
                    mapObject.remove(mapObject.get(i));
                }
                if(t2.getRec().intersects(mapObject.get(i).getRec())) {
                    t2.livesUp();
                    mapObject.remove(mapObject.get(i));
                }
            }
        }

        //tank interacts with bullets
        t1.bulletHitTank(t2);
        t2.bulletHitTank(t1);
    }



}
