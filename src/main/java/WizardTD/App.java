package WizardTD;

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

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public boolean[] clickedBoxes = new boolean[7]; // An array to represent the clicked state of each box

    public boolean FFkeyOn = false;
    public boolean PkeyOn = false;
    public boolean TkeyOn = false;
    public boolean U1keyOn = false;
    public boolean U2keyOn = false;
    public boolean U3keyOn = false;
    public boolean MkeyOn = false;

    public String configPath;
    public String layoutFile;
    public int initialTowerRange;
    public float initialTowerFiringSpeed;
    public int initialTowerDamage;
    public int initialMana;
    public int initialManaCap;
    public int initialManaGainedPerSecond;
    public int towerCost;
    public int manaPoolSpellInitialCost;
    public int manaPoolSpellCostIncreasePerUse;
    public float manaPoolSpellCapMultiplier;
    public float manaPoolSpellManaGainedMultiplier;
    
    public JSONArray waves;
    public int CurWaveNumber = 1;
    public int totalWaves;
    public ArrayList<Wave> waveList;

    public Random random = new Random();

    // Stores a 2D array containing the game board's layout (non-moving objects)
    public ArrayList<ArrayList<main.java.WizardTD.GameObject>> GameObjectsList = new ArrayList<ArrayList<main.java.WizardTD.GameObject>>();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }
    


	@Override
    public void setup() {
        frameRate(FPS);
    
        /// JSON FILE ///
        JSONObject gameJSON = new JSONObject();
        String fileContents = "";
        try {
            fileContents = new String(Files.readAllBytes(Paths.get(configPath)));
            gameJSON = JSONObject.parse(fileContents); // Parse the JSON
        } catch (IOException e) {
            // Handle file IO errors here, e.g., print an error message
            e.printStackTrace();
        }
    
        /// SPECS IN JSON FILE ///
        this.layoutFile = gameJSON.getString("layout");
        this.waves = gameJSON.getJSONArray("waves");
        this.initialTowerRange = gameJSON.getInt("initial_tower_range");
        this.initialTowerFiringSpeed = gameJSON.getFloat("initial_tower_firing_speed");
        this.initialTowerDamage = gameJSON.getInt("initial_tower_damage");
        this.initialMana = gameJSON.getInt("initial_mana");
        this.initialManaCap = gameJSON.getInt("initial_mana_cap");
        this.initialManaGainedPerSecond = gameJSON.getInt("initial_mana_gained_per_second");
        this.towerCost = gameJSON.getInt("tower_cost");
        this.manaPoolSpellInitialCost = gameJSON.getInt("mana_pool_spell_initial_cost");
        this.manaPoolSpellCostIncreasePerUse = gameJSON.getInt("mana_pool_spell_cost_increase_per_use");
        this.manaPoolSpellCapMultiplier = gameJSON.getFloat("mana_pool_spell_cap_multiplier");
        this.manaPoolSpellManaGainedMultiplier = gameJSON.getFloat("mana_pool_spell_mana_gained_multiplier");

        /// LOADING IMAGES ///
        loadImage("src/main/resources/WizardTD/beetle.png");
        loadImage("src/main/resources/WizardTD/worm.png");
        loadImage("src/main/resources/WizardTD/gremlin.png");
        loadImage("src/main/resources/WizardTD/gremlin1.png");
        loadImage("src/main/resources/WizardTD/gremlin2.png");
        loadImage("src/main/resources/WizardTD/gremlin3.png");
        loadImage("src/main/resources/WizardTD/gremlin4.png");
        loadImage("src/main/resources/WizardTD/gremlin5.png");
        loadImage("src/main/resources/WizardTD/path0.png");
        loadImage("src/main/resources/WizardTD/path1.png");
        loadImage("src/main/resources/WizardTD/path2.png");
        loadImage("src/main/resources/WizardTD/path3.png");
        loadImage("src/main/resources/WizardTD/grass.png");
        loadImage("src/main/resources/WizardTD/shrub.png");
        loadImage("src/main/resources/WizardTD/tower0.png");
        loadImage("src/main/resources/WizardTD/tower1.png");
        loadImage("src/main/resources/WizardTD/tower2.png");
        loadImage("src/main/resources/WizardTD/fireball.png");
        loadImage("src/main/resources/WizardTD/wizard_house.png");


        // READING MAP LAYOUT SYMBOLS ///
        try {
            char[][] fileData = GetFileTextAs2DArray(layoutFile);
    
            // Now you can work with the 'fileData' 2D array
            // For example, you can loop through it and process the contents
            int curRow = 0;
            for (char[] row : fileData) {
                ArrayList<main.java.WizardTD.GameObject> rowList = new ArrayList<main.java.WizardTD.GameObject>();
                int curCol = 0;
                for (char c : row) {
                    int[] coordinates = getCoords(curRow, curCol);
                    int coordX = coordinates[0];
                    int coordY = coordinates[1];
                    main.java.WizardTD.GameObject curGameObject = new GameObject(coordX, coordY);
                    // Adding correct image file and loading this file
                    String objectString = String.valueOf(c);
                    if(objectString.equals(" ")){
                        curGameObject.setSprite(this.loadImage("src/main/resources/WizardTD/grass.png"));
                    } else if (objectString.equals("S")) {
                        curGameObject.setSprite(this.loadImage("src/main/resources/WizardTD/shrub.png"));
                    } else if (objectString.equals("X")) {
                        int[] pathValues = LoadPath(fileData, curRow, curCol);
                        int pathType = pathValues[0] - 1;
                        String pathTypeString = String.valueOf(pathType);
                        double rotateAngle = (double) pathValues[1];
                        String pathFilePath = "src/main/resources/WizardTD/path" + pathTypeString + ".png";
                        PImage loadPathImage = loadImage(pathFilePath);
                        PImage rotatedImage = rotateImageByDegrees(loadPathImage, rotateAngle);
                        curGameObject.setSprite(rotatedImage);

                    } else if (objectString.equals("W")) {
                        coordX -= 8; // Making adjustments for wizard tower
                        coordY -= 8;
                        curGameObject = new GameObject(coordX, coordY);
                        curGameObject.setName("W");
                        curGameObject.setSprite(this.loadImage("src/main/resources/WizardTD/wizard_house.png"));

                        //curGameObject.setSprite(this.loadImage("src/main/resources/WizardTD/wizard_house.png"));
                    } else {
                        throw new FileNotFoundException("Image file not found.");
                    }
                    rowList.add(curGameObject);
                    curCol++;
                }
                GameObjectsList.add(rowList);
                curRow++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., file not found, file read error)
        }


        waveList = new ArrayList<Wave>(waves.size());
        totalWaves = waves.size();

        // Creating waves list and placing it in waveInstances list
        for (int i = 0; i < waves.size(); i++) {
            JSONObject thisWave = waves.getJSONObject(i);
            int dur = thisWave.getInt("duration");
            double preWaveTime = thisWave.getDouble("pre_wave_pause");
            JSONArray monster = thisWave.getJSONArray("monsters");
            Wave curWave = new Wave(dur, preWaveTime, monster);
            waveList.add(curWave);
        }

    }
    
    
    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){

        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        if (key == 'f') {
            clickedBoxes[0] = !clickedBoxes[0]; // Toggle the clicked state
            FFkeyOn = !FFkeyOn;
        }
        if (key == 'p') {
            clickedBoxes[1] = !clickedBoxes[1];
            PkeyOn = !PkeyOn;
        }
        if (key == 't') {
            clickedBoxes[2] = !clickedBoxes[2];
            TkeyOn = !TkeyOn;
        }
        if (key == '1') {
            clickedBoxes[3] = !clickedBoxes[3];
            U1keyOn = !U1keyOn;
        }
        if (key == '2') {
            clickedBoxes[4] = !clickedBoxes[4];
            U2keyOn = !U2keyOn;
        }
        if (key == '3') {
            clickedBoxes[5] = !clickedBoxes[5];
            U3keyOn = !U3keyOn;
        }
        if (key == 'm') {
            clickedBoxes[6] = !clickedBoxes[6];
            MkeyOn = !MkeyOn;
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {
        boolean isMouseOverFFBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 47 && mouseY <= 47 + 46;
        boolean isMouseOverPBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 107 && mouseY <= 107 + 46;
        boolean isMouseOverTBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 167 && mouseY <= 167 + 46;
        boolean isMouseOverU1Box = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 227 && mouseY <= 227 + 46;
        boolean isMouseOverU2Box = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 287 && mouseY <= 287 + 46;
        boolean isMouseOverU3Box = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 347 && mouseY <= 347 + 46;
        boolean isMouseOverMBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 407 && mouseY <= 407 + 46;
        

        if (isMouseOverFFBox) {
            clickedBoxes[0] = !clickedBoxes[0]; // Toggle the clicked state
            FFkeyOn = !FFkeyOn;
        }
        if (isMouseOverPBox) {
            clickedBoxes[1] = !clickedBoxes[1];
            PkeyOn = !PkeyOn;
        }
        if (isMouseOverTBox) {
            clickedBoxes[2] = !clickedBoxes[2];
            TkeyOn = !TkeyOn;

        }
        if (isMouseOverU1Box) {
            clickedBoxes[3] = !clickedBoxes[3];
            U1keyOn = !U1keyOn;
        }
        if (isMouseOverU2Box) {
            clickedBoxes[4] = !clickedBoxes[4];
            U2keyOn = !U2keyOn;
        }
        if (isMouseOverU3Box) {
            clickedBoxes[5] = !clickedBoxes[5];
            U3keyOn = !U3keyOn;
        }
        if (isMouseOverMBox) {
            clickedBoxes[6] = !clickedBoxes[6];
            MkeyOn = !MkeyOn;
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {

    }


    @Override
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() { // Function is called once every second
        background(143,122,86);

        // Define colors without capitalizing the first letter
        int boxColor = color(255, 0); // Default color
        int hoverColor = color(200); // Color when hovered
        int clickedColor = color(255, 255, 0); // Yellow color when clicked

        boolean isMouseOverFFBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 47 && mouseY <= 47 + 46;
        boolean isMouseOverPBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 107 && mouseY <= 107 + 46;
        boolean isMouseOverTBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 167 && mouseY <= 167 + 46;
        boolean isMouseOverU1Box = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 227 && mouseY <= 227 + 46;
        boolean isMouseOverU2Box = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 287 && mouseY <= 287 + 46;
        boolean isMouseOverU3Box = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 347 && mouseY <= 347 + 46;
        boolean isMouseOverMBox = mouseX >= 647 && mouseX <= 647 + 43 && mouseY >= 407 && mouseY <= 407 + 46;

        // Set box colors based on hover state
        // First checks if button is clicked (?), then :, checks another condition, being hovering, otherwise it is just boxColor
        int ffBoxColor = (clickedBoxes[0]) ? clickedColor : (isMouseOverFFBox ? hoverColor : boxColor);
        int pBoxColor = (clickedBoxes[1]) ? clickedColor : (isMouseOverPBox ? hoverColor : boxColor);
        int tBoxColor = (clickedBoxes[2]) ? clickedColor : (isMouseOverTBox ? hoverColor : boxColor);
        int u1BoxColor = (clickedBoxes[3]) ? clickedColor : (isMouseOverU1Box ? hoverColor : boxColor);
        int u2BoxColor = (clickedBoxes[4]) ? clickedColor : (isMouseOverU2Box ? hoverColor : boxColor);
        int u3BoxColor = (clickedBoxes[5]) ? clickedColor : (isMouseOverU3Box ? hoverColor : boxColor);
        int mBoxColor = (clickedBoxes[6]) ? clickedColor : (isMouseOverMBox ? hoverColor : boxColor);
        
        // Draw the boxes with the determined colors
        StaticBox FFBox = new StaticBox(647, 47, 43, 46, 2, ffBoxColor, color(0));
        FFBox.draw(this);

        StaticBox PBox = new StaticBox(647, 107, 43, 46, 2, pBoxColor, color(0));
        PBox.draw(this);

        StaticBox TBox = new StaticBox(647, 167, 43, 46, 2, tBoxColor, color(0));
        TBox.draw(this);

        StaticBox U1Box = new StaticBox(647, 227, 43, 46, 2, u1BoxColor, color(0));
        U1Box.draw(this);

        StaticBox U2Box = new StaticBox(647, 287, 43, 46, 2, u2BoxColor, color(0));
        U2Box.draw(this);

        StaticBox U3Box = new StaticBox(647, 347, 43, 46, 2, u3BoxColor, color(0));
        U3Box.draw(this);

        StaticBox MBox = new StaticBox(647, 407, 43, 46, 2, mBoxColor, color(0));
        MBox.draw(this);

        
        
        /// TOP WHITE MANA BAR ///
        StaticBox WhiteMANABox = new StaticBox(400, 8, 320, 24, 2, color(255), color(0));
        WhiteMANABox.draw(this);

        // 1 pixel = 3.125 mana
        double MANABlueBarWidth = (double) this.initialMana / 3.125;
        StaticBox MBoxBlue = new StaticBox(400, 8, (int) MANABlueBarWidth, 24, 2, color(8,212,220), color(0));
        MBoxBlue.draw(this);

        String initialManaString = String.valueOf(this.initialMana);

        StaticText ManaTextTotal = new StaticText(550, 30, 26, "/ 1000", "Arial");
        ManaTextTotal.draw(this);
        StaticText ManaTextCurrentAmount = new StaticText(500, 30, 26, initialManaString, "Arial");
        ManaTextCurrentAmount.draw(this);        


        for (ArrayList<main.java.WizardTD.GameObject> row : GameObjectsList) {
            for (main.java.WizardTD.GameObject obj : row) {
                //obj.tick();
                obj.draw(this);
            }
        }

        
        // Re-drawing wizard tower so it is above all other objects
        for (ArrayList<main.java.WizardTD.GameObject> rowX : GameObjectsList) {
            for (main.java.WizardTD.GameObject objX : rowX) {
                if ("W".equals(objX.objName)) {
                    objX.draw(this);
                }
            }
        }

        /// TEXT ///
        /* 
         * FIX!!!
         * Not doing pre wave pause for wave 3
         * Need to fix first time number not appearing, as it skips straight down to second
        */
        if (CurWaveNumber < waveList.size()) {
            Wave curWaveInfo = waveList.get(CurWaveNumber - 1);
            //curWaveInfo.PrintWaveInfo();
            if (!curWaveInfo.getPreWavePauseComplete()) {
                StaticText WaveText = new StaticText(5, 27, 22, "Wave " + CurWaveNumber, "Arial");
                StaticText StartsText = new StaticText(90, 27, 22, "starts: 0", "Arial");
                WaveText.draw(this);
                StartsText.draw(this);
            } else if (curWaveInfo.getPreWavePauseComplete() && !curWaveInfo.getWaveComplete()) {
                StaticText WaveText = new StaticText(5, 27, 22, "Wave " + (CurWaveNumber + 1), "Arial");
                StaticText StartsText = new StaticText(90, 27, 22, "starts: " + (int) Math.round(curWaveInfo.getDurationLeft()), "Arial");
                WaveText.draw(this);
                StartsText.draw(this);
            } else {
                CurWaveNumber++;
            }
            curWaveInfo.tick(); // Going to next time
        }

        /*
        if (curWaveInfo.getPreWavePauseComplete()) {
            CurWaveNumber++;
        } else {
            if (CurWaveNumber <= totalWaves) {
                StaticText WaveText;
                StaticText StartsText;

                if (curWaveInfo.getPreWavePauseLeft() > 0) {
                    WaveText = new StaticText(5, 27, 22, "Wave " + CurWaveNumber, "Arial");
                    StartsText = new StaticText(90, 27, 22, "starts: 0", "Arial");
                    WaveText.draw(this);
                    StartsText.draw(this);
                } else {
                    WaveText = new StaticText(5, 27, 22, "Wave " + (CurWaveNumber + 1), "Arial");
                    StartsText = new StaticText(90, 27, 22, "starts: " + (int) Math.round(curWaveInfo.getDurationLeft()), "Arial");
                    WaveText.draw(this);
                    StartsText.draw(this);
                }
            }
        }
        */

        

        StaticText MANAText = new StaticText(320, 27, 20, "MANA:", "Arial");
        MANAText.draw(this);

        StaticText FFText = new StaticText(653, 80, 28, "FF", "Arial-Bold");
        FFText.draw(this);
        StaticText SpeedText = new StaticText(695, 65, 13, "2x speed", "Arial");
        SpeedText.draw(this);

        StaticText PText = new StaticText(653, 140, 28, "P", "Arial-Bold");
        PText.draw(this);
        StaticText PAUSEText = new StaticText(695, 125, 13, "PAUSE", "Arial");
        PAUSEText.draw(this);

        StaticText TText = new StaticText(653, 200, 28, "T", "Arial-Bold");
        TText.draw(this);
        StaticText BuildTowerText = new StaticText(693, 185, 13, "Build\ntower", "Arial");
        BuildTowerText.draw(this);

        StaticText U1Text = new StaticText(652, 260, 28, "U1", "Arial-Bold");
        U1Text.draw(this);
        StaticText UpgradeRangeText = new StaticText(695, 245, 13, "Upgrade\nrange", "Arial");
        UpgradeRangeText.draw(this);

        StaticText U2Text = new StaticText(652, 320, 28, "U2", "Arial-Bold");
        U2Text.draw(this);
        StaticText UpgradeSpeedText = new StaticText(695, 305, 13, "Upgrade\nspeed", "Arial");
        UpgradeSpeedText.draw(this);

        StaticText U3Text = new StaticText(652, 380, 28, "U3", "Arial-Bold");
        U3Text.draw(this);
        StaticText UpgradeDamageText = new StaticText(695, 365, 13, "Upgrade\ndamage", "Arial");
        UpgradeDamageText.draw(this);

        StaticText MText = new StaticText(653, 440, 28, "M", "Arial-Bold");
        MText.draw(this);
        StaticText ManaPoolText = new StaticText(695, 425, 13, "Mana pool\ncost: 100", "Arial");
        ManaPoolText.draw(this);

    } 



    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }


    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, RGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }



    public int[] getCoords(int row, int col) {
        int x = col * 32;
        int y = 40 + row * 32;
        return new int[]{x, y};
    }


    // Returns a list of [type_of_turn, rotation_angle_to_turn_clockwise]
    // 1 = straight, 2 = curve, 3 = 3-way turn, 4 = 4-way turn
    public int[] LoadPath(char[][] map2DList, int row, int col){
        boolean hasLeftPath = col > 0 && map2DList[row][col - 1] == 'X';
        boolean hasRightPath = col < map2DList[0].length - 1 && map2DList[row][col + 1] == 'X';
        boolean hasTopPath = row > 0 && map2DList[row - 1][col] == 'X';
        boolean hasBottomPath = row < map2DList.length - 1 && map2DList[row + 1][col] == 'X';
        int[] returnValues = new int[2];

        if (hasLeftPath && hasRightPath && hasTopPath && hasBottomPath){
            returnValues[0] = 4;
            returnValues[1] = 0;
        } else if (hasLeftPath && hasRightPath && hasTopPath) {
            returnValues[0] = 3;
            returnValues[1] = 180;
        } else if (hasRightPath && hasTopPath && hasBottomPath) {
            returnValues[0] = 3;
            returnValues[1] = 270;
        } else if (hasTopPath && hasBottomPath && hasLeftPath) {
            returnValues[0] = 3;
            returnValues[1] = 90;
        } else if (hasBottomPath && hasLeftPath && hasRightPath) {
            returnValues[0] = 3;
            returnValues[1] = 0;
        } else if (hasLeftPath && hasRightPath) {
            returnValues[0] = 1;
            returnValues[1] = 0;
        } else if (hasTopPath && hasBottomPath) {
            returnValues[0] = 1;
            returnValues[1] = 90;
        } else if (hasLeftPath && hasBottomPath) {
            returnValues[0] = 2;
            returnValues[1] = 0;
        } else if (hasRightPath && hasBottomPath) {
            returnValues[0] = 2;
            returnValues[1] = 270;
        } else if (hasRightPath && hasTopPath) {
            returnValues[0] = 2;
            returnValues[1] = 180;
        } else if (hasLeftPath && hasTopPath) {
            returnValues[0] = 2;
            returnValues[1] = 90;
        } else if (hasRightPath || hasLeftPath) {
            returnValues[0] = 1;
            returnValues[1] = 0;
        }  else if (hasTopPath || hasBottomPath) {
            returnValues[0] = 1;
            returnValues[1] = 90;
        } else {
            returnValues[0] = 1;
            returnValues[1] = 0;
        }

        return returnValues;
    }



    public char[][] GetFileTextAs2DArray(String filename) throws IOException {
        List<char[]> rows = new ArrayList<>();
        int maxLength = 0;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                char[] row = line.toCharArray();
                rows.add(row);
                // Update the maximum length if this row is longer
                maxLength = Math.max(maxLength, row.length);
            }
        }
    
        // Pad shorter rows with spaces to match the maximum length
        for (int i = 0; i < rows.size(); i++) {
            char[] row = rows.get(i);
            if (row.length < maxLength) {
                char[] paddedRow = new char[maxLength];
                Arrays.fill(paddedRow, ' '); // Fill with spaces
                System.arraycopy(row, 0, paddedRow, 0, row.length); // Copy the original content
                rows.set(i, paddedRow); // Replace the original row with the padded row
            }
        }
    
        char[][] result = new char[rows.size()][];
        rows.toArray(result);
        return result;
    }


}
