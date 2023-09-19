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

import main.java.WizardTD.GameObject;
import main.java.WizardTD.StaticText;
import main.java.WizardTD.MovingObject;

public class StaticText {
    // Set text properties
    //textFont(createFont("Arial", 20));
    //textSize(20);
    //fill(0); // Text color (black)
    
    // Draw the text directly on the main canvas
    //text("Hello", 20, 40); // Text at (20, 40)

    public int x;
    public int y;
    public int fontSize;
    public String text;
    public String textFont;


    public StaticText(int x, int y, int fontSize, String text, String textFont) {
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.text = text;
        this.textFont = textFont; 
    }

    public void draw(PApplet app) {
        PFont curTextFont = app.createFont(textFont, fontSize);
        app.textFont(curTextFont);
        app.fill(0);
        app.text(text, x, y);
    }
}
