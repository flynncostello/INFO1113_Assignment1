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



public class Wave {
    public App app;

    public double duration;
    public double durationLeft;
    public double pre_wave_pause;
    public double pre_wave_pauseLeft;
    public boolean waveComplete = false;
    public boolean preWavePauseComplete = false;
    public static final double SECONDS_BETWEEN_CHANGES = 1.0 / 60.0;

    /// INFO FOR MONSTERS ///
    public JSONArray monsters; /// OVERALL ARRAY FOR MONSTERS, EACH ELEMENT IS A JSON Object
    
    public int FramesBetweenMonsterSpawn; // How many frames seperate each monster spawn in
    public ArrayList<SpawnAndPath> MonsterPaths; // All possible spawn locations
    
    public int totalMonsters;
    public int monstersLeft;
    public int numberOfDifferentMonsters;
    
    public ArrayList<Monster> ActiveMonsters = new ArrayList<>(); // Monsters drawn on screen

    // Checking time every tenth of a second but keeps whole numbers by rounding times later
    private int timer;
    private int monsterTimer;
    private boolean firstLoop = true;
    private int waveNumber;


    public Wave(ArrayList<SpawnAndPath> MonsterPaths, App app, double duration, double pre_wave_pause, JSONArray monsters) {
        this.duration = duration; // Needs to be double 
        this.durationLeft = duration;
        this.pre_wave_pause = pre_wave_pause;
        this.pre_wave_pauseLeft = pre_wave_pause;

        this.monsters = monsters;
        this.numberOfDifferentMonsters = monsters.size();
        this.MonsterPaths = MonsterPaths;

        this.timer = 0;
        this.monsterTimer = 0;

        this.app = app;

        this.totalMonsters = 0;

        for (int i = 0; i < this.monsters.size(); i++) {
            JSONObject curMonsterGroupInfo = this.monsters.getJSONObject(i);
            int quantity = curMonsterGroupInfo.getInt("quantity");
            this.totalMonsters += quantity;
        }
        this.monstersLeft = this.totalMonsters;
    }

    public void setWaveNumber(int waveNum) {
        this.waveNumber = waveNum;
    }

    public int getWaveNumber() {
        return this.waveNumber;
    }

    public ArrayList<Monster> getActiveMonsters() {
        return this.ActiveMonsters;
    }

    public int getMonstersLeftToPlace() {
        return this.monstersLeft;
    }

    public void calcFramesBetweenMonsterSpawns() {
        this.FramesBetweenMonsterSpawn = (int)((this.duration / this.totalMonsters) * App.FPS);
        //System.out.println(FramesBetweenMonsterSpawn);
    }


    public void tick() {
        if (firstLoop) {
            firstLoop = false;
        } else {
            if (!app.PkeyOn) {
                this.timer++;
            }
        }

        /// CHANGING IN-GAME TIMER ///
        if (!waveComplete && timer == 1 && !app.PkeyOn) {
            timer = 0;
            if (pre_wave_pauseLeft > 0) {
                if (app.FFkeyOn) {
                    pre_wave_pauseLeft -= SECONDS_BETWEEN_CHANGES * 2;
                } else {
                    pre_wave_pauseLeft -= SECONDS_BETWEEN_CHANGES;
                }
            } else if (durationLeft > 0) {
                if (app.FFkeyOn) {
                    durationLeft -= SECONDS_BETWEEN_CHANGES * 2;
                } else {
                    durationLeft -= SECONDS_BETWEEN_CHANGES;
                }
                preWavePauseComplete = true;
            } else {
                waveComplete = true;
            }
        }

        boolean monstersActive = app.hasMonstersStillMoving(ActiveMonsters);
        
        /// MONSTERS ///
        if (preWavePauseComplete && (monstersActive || getMonstersLeftToPlace() > 0)) {
            if (!app.PkeyOn) {
                if (app.FFkeyOn) {
                    this.monsterTimer += 2;
                } else {
                    this.monsterTimer++;
                }
                if (monsterTimer >= FramesBetweenMonsterSpawn) {
                    this.monsterTimer = 0;
                
                    if (getMonstersLeftToPlace() != 0) {
                        // SPAWNING MONSTERS //
                        Random random = new Random();
                        int randomIndex;
                        JSONObject chosenMonster;
                        do {
                            randomIndex = random.nextInt(numberOfDifferentMonsters);
                            chosenMonster = monsters.getJSONObject(randomIndex);
                        } while (chosenMonster.getInt("quantity") == 0);
                        

                        int originalQuantity = chosenMonster.getInt("quantity");

                        String type = chosenMonster.getString("type");
                        int hp = chosenMonster.getInt("hp");
                        double speed = chosenMonster.getDouble("speed");
                        double armour = chosenMonster.getDouble("armour");
                        int ManaGainedOnKill = chosenMonster.getInt("mana_gained_on_kill");

                        Monster newMonster = new Monster(MonsterPaths, app, type, hp, speed, armour, ManaGainedOnKill);
                        ActiveMonsters.add(newMonster);
                        monstersLeft -= 1;

                        // Changing quantity left for this specific monster type in overall monsters JSONArray
                        chosenMonster.setInt("quantity", originalQuantity - 1);
                        this.monsters.setJSONObject(randomIndex, chosenMonster);
                    }
                }
            }
            
            // UPDATING EXISTING MONSTERS //
            for (int i = 0; i < this.ActiveMonsters.size(); i++) {
                Monster curMonster = ActiveMonsters.get(i);
                // Checking if monster reached wizard house
                if (curMonster.reachedWizardTower()) {
                    app.setMana(app.getMana() - curMonster.getHp());
                    curMonster.respawn();
                }

                // Checking if monster was killed (has not reached wizard house)
                if (!curMonster.CheckIsAlive()) {
                    app.addMana(curMonster.getManaGainedOnKill());
                    ActiveMonsters.remove(i);
                }

                if (!app.PkeyOn) {
                    curMonster.takeDamage(1);
                    curMonster.move();
                }
                curMonster.draw(app);
            }
        }
    }
   
    
    public boolean getWaveComplete() {
        return waveComplete;
    }

    public boolean getPreWavePauseComplete() {
        return preWavePauseComplete;
    }

    public int getDurationLeft() {
        return (int) Math.ceil(durationLeft);
    }

    public double getPreWavePauseLeft() {
        return pre_wave_pauseLeft;
    }

    public void PrintWaveInfo() {
        System.out.println("Duration: " + duration);
        System.out.println("Duration left: " + durationLeft);
        System.out.println("Pre wave pause: " + pre_wave_pause);
        System.out.println("Pre wave pause left: " + pre_wave_pauseLeft);
        System.out.println("Wave complete: " + waveComplete);
        System.out.println("Timer: " + timer);
    }
}

/*
Wave 1 start: 0 (for 0.5 seconds)
Wave 2 starts: 8 (for 8 seconds)
Wave 2 starts: 0 (for 10 seconds)
Wave 3 starts: 5 (for 5 seconds)
Wave 3 starts: 0 (for 10 seconds)
Then nothing as wave 3 is last wave
*/
