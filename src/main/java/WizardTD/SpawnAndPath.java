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


public class SpawnAndPath {
    private LinkedList<Point> curPathSolution;
    private JSONArray curSpawnLocationPixels;

    public SpawnAndPath(LinkedList<Point> curPathSolution, JSONArray curSpawnLocationPixels) {
        this.curPathSolution = curPathSolution;
        this.curSpawnLocationPixels = curSpawnLocationPixels;
    }

    public LinkedList<Point> getSolution() {
        return curPathSolution;
    }

    public JSONArray getSpawnLocation() {
        return curSpawnLocationPixels;
    }
    
}
