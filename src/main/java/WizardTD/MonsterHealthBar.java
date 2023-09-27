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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import main.java.WizardTD.*;
import processing.core.*;

public class MonsterHealthBar {
    private double greenX;
    private double greenY;
    private double greenWidth;
    private double greenHeight = 3.0;

    private double redX;
    private double redY;
    private double redWidth;
    private double redHeight = 3.0;

    private int hp;
    private int originalHP;
    private double healthPerPixel;

    public MonsterHealthBar(double greenX, double greenY, double redX, double redY, int hp, int originalHP) {
        this.greenX = greenX;
        this.greenY = greenY;
        this.redX = redX;
        this.redY = redY;
        this.hp = hp;
        this.originalHP = originalHP;
        this.healthPerPixel = (double) this.originalHP / 32.0;
        this.greenWidth = (double) this.hp / this.healthPerPixel;
        this.redWidth = 32.0 - this.greenWidth;
    }

    public void setXY(double newX, double newY, int HP, int origHP) {
      this.greenX = newX;
      this.greenY = newY - 5;

      this.redX = newX + 32.0;
      this.redY = newY - 5;

      this.hp = HP;
      this.originalHP = origHP;

      /*
      System.out.println(this.greenX);
      System.out.println(this.greenY);
      System.out.println(this.redX);
      System.out.println(this.redY);
      System.out.println(this.hp);
      System.out.println(this.originalHP);
      System.out.println(this.redWidth);
      System.out.println(this.redHeight);
      System.out.println(this.greenWidth);
      System.out.println(this.greenHeight);
      */
    }

    public void changeXY(double xChange, double yChange) {
        this.greenX += xChange;
        this.redX += xChange;

        this.greenY += yChange;
        this.redY += yChange;
    }

    public void setHealth(double newHP) {
      this.greenWidth -= newHP / this.healthPerPixel;
      this.redX -= newHP / this.healthPerPixel;
      this.redWidth += newHP / this.healthPerPixel;
  
      // Ensuring the greenWidth doesn't go below 0
      if (this.greenWidth < 0) {
          this.greenWidth = 0;
      }
  
      // Ensuring the redWidth doesn't go above total health bar width
      if (this.redWidth > 32.0) {
          this.redWidth = 32.0;
      }
  }
  

    public void draw(PApplet app) {
        app.noStroke();
        app.fill(0, 245, 0);
        app.rect((float) this.greenX, (float) this.greenY, (float) this.greenWidth, (float) this.greenHeight);

        app.fill(245, 0, 0);
        app.rect((float) this.redX, (float) this.redY, (float) this.redWidth, (float) this.redHeight);
    }

    public double getGreenX() {
      return this.greenX;
    }

    public double getGreenY() {
      return this.greenY;
    }

    public double getRedX() {
      return this.redX;
    }

    public double getRedY() {
      return this.redY;
    }
}
