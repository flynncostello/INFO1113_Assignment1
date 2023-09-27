package main.java.WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Point;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import WizardTD.App;
import main.java.WizardTD.*;


public class Monster {
    private String type;
    private int hp;
    private int originalHP;

    private double speed;
    private double armour;
    private int ManaGainedOnKill;

    private int x;
    private int originalX;
    private int y;
    private int originalY;

    private boolean isAlive = true;
    private MonsterHealthBar healthBar;

    private ArrayList<ArrayList<GameObject>> GameObjectsList;

    private ArrayList<SpawnAndPath> MonsterPaths;    
    private JSONArray MonsterSpawnLocation; // [1, 0]
    private LinkedList<Point> pathToDestination; // [[1, 0], [1, 0], [0, 1],...]
    
    private int randomSpawnLocationNum;
    private SpawnAndPath curSpawnAndPoint;

    private int moveCount = 0;

    private PImage sprite;
    public App app;

    public int counter = 0;

    public Monster(ArrayList<SpawnAndPath> MonsterPaths, App app, String type, int hp, double speed, double armour, int ManaGainedOnKill) { //, LinkedList<Point> pathToDestination
        this.MonsterPaths = MonsterPaths;
        this.app = app;
        this.type = type;

        this.hp = hp;
        this.originalHP = hp;

        this.speed = speed;
        this.armour = armour;
        this.ManaGainedOnKill = ManaGainedOnKill;
        this.sprite = app.imageMap.get(type);
        this.GameObjectsList = app.GameObjectsList;

        this.randomSpawnLocationNum = app.random.nextInt(this.MonsterPaths.size());
        this.curSpawnAndPoint = MonsterPaths.get(randomSpawnLocationNum);
        this.MonsterSpawnLocation = this.curSpawnAndPoint.getSpawnLocation();
        this.pathToDestination = this.curSpawnAndPoint.getSolution();
       
        this.x = this.MonsterSpawnLocation.getInt(0);
        this.originalX = this.MonsterSpawnLocation.getInt(0);
        this.y = this.MonsterSpawnLocation.getInt(1);
        this.originalY = this.MonsterSpawnLocation.getInt(1);

    
        this.healthBar = new MonsterHealthBar((double) this.x, (double) this.y - 5, (double) this.x + 32, (double) this.y - 5, this.hp, this.originalHP);
    }

    public void takeDamage(double damage) {
        double effectiveDamage = damage * this.getArmour();
        this.hp -= effectiveDamage; // Health reduction also impacted by armour
        this.healthBar.setHealth(this.hp);
    }

    
    public void move() {
        int pixelsMoved = 0;
        double curSpeed = app.FFkeyOn ? this.speed * 2 : this.speed;
        //System.out.println(curSpeed);

        while (pixelsMoved < curSpeed) {
            int oldX = this.x;
            int oldY = this.y;

            Point newPos = (Point) pathToDestination.get(moveCount);

            int changeInX = newPos.y * 32 - this.x; // y as in vertical axis
            int changeInXAbs = Math.abs(newPos.y * 32 - this.x); // y as in vertical axis
            int changeInY = (40 + newPos.x * 32) - this.y; // x as in horizontal axis
            int changeInYAbs = Math.abs((40 + newPos.x * 32) - this.y); // x as in horizontal axis

            if (changeInXAbs < curSpeed && changeInYAbs < curSpeed) { // Means we need to do multiple jumps
                
                // Is saying that:
                // - The change in pixels from 1 cell to the next is not enough
                // - To cover speed, therefore we need to make multiple cell jumps
            
                this.x += changeInX;
                this.y += changeInY;
                pixelsMoved += (changeInXAbs + changeInYAbs);
                this.moveCount++;
            } else {
                
                // When change between cells is too much compared to 
                
                double negativeSpeed = -curSpeed;
                if (changeInX < 0) {
                    this.x -= curSpeed;
                } else if (changeInX > 0) {
                    this.x += curSpeed;
                } else if (changeInY < 0) {
                    this.y -= curSpeed;
                } else if (changeInY > 0) {
                    this.y += curSpeed;
                }
                pixelsMoved += curSpeed;
                // Don't need to increment moveCount as we haven't reached next cell yet
            }
            this.healthBar.changeXY(this.x - oldX, this.y - oldY);
        }

        //System.out.println(this.hp);
    }

    public void draw(PApplet app) {
        this.app.image(this.sprite, this.x + 5, this.y);
        this.healthBar.draw(this.app);
    }


    public boolean reachedWizardTower() {
        return (x <= app.destX + 16 && x >= app.destX - 16) && (y <= app.destY + 16 && y >= app.destY - 16);   
    }

    public void respawn() {
        this.x = this.originalX;
        this.y = this.originalY;
        this.moveCount = 0;
        this.healthBar.setXY(this.originalX, this.originalY, this.hp, this.originalHP);
    }

    public boolean CheckIsAlive() {
        if (hp <= 0) {
            isAlive = false;
        }

        return isAlive;
    }

    /// GETTERS AND SETTERS ///

    // Getter and Setter methods for type
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter and Setter methods for hp
    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    // Getter and Setter methods for speed
    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // Getter and Setter methods for armour
    public double getArmour() {
        return this.armour;
    }

    public void setArmour(double armour) {
        this.armour = armour;
    }

    // Getter and Setter methods for ManaGainedOnKill
    public int getManaGainedOnKill() {
        return this.ManaGainedOnKill;
    }

    public void setManaGainedOnKill(int ManaGainedOnKill) {
        this.ManaGainedOnKill = ManaGainedOnKill;
    }
}
