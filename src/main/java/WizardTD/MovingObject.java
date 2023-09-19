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
public abstract class MovingObject {
    protected int x;
    protected int y;
    public String objName = null;

    private PImage sprite;

    public MovingObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setName(String name) {
        this.objName = name;
    }

    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }


    public abstract void tick();


    public void draw(PApplet app) {
        app.image(this.sprite, this.x, this.y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
