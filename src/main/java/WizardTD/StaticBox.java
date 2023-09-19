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
import processing.core.*;

public class StaticBox {
  private int x;
  private int y;
  private int width;
  private int height;
  private int borderThickness;
  private int fillColor;
  private int borderColor;

  public StaticBox(int x, int y, int width, int height, int borderThickness, int fillColor, int borderColor) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.borderThickness = borderThickness;
    this.fillColor = fillColor;
    this.borderColor = borderColor;
  }

  public void draw(PApplet app) {
    app.stroke(borderColor);
    app.strokeWeight(borderThickness);
    app.fill(fillColor);
    app.rect(x, y, width, height);
  }
}
