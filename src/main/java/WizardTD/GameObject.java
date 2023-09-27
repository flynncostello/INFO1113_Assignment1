package main.java.WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
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

/**
 * Represents an abstract Game object object that is inherited by all non-moving objects
 */
public class GameObject {
    protected int x;
    protected int y;
    public char objName;

    private PImage sprite;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setName(char name) {
        this.objName = name;
    }

    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }

    // Don't need tick method as these objects won't change during the game
    //public abstract void tick();


    public void draw(PApplet app) {
        // The image() method is used to draw PImages onto the screen.
        // The first argument is the image, the second and third arguments are coordinates
        app.image(this.sprite, this.x, this.y);
    }

    public char getName() {
        return this.objName;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
