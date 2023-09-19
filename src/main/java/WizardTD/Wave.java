package main.java.WizardTD;

import processing.core.PApplet;
import processing.data.JSONArray;
import WizardTD.App;


public class Wave {
    public double duration;
    public double durationLeft;
    public double pre_wave_pause;
    public double pre_wave_pauseLeft;
    public JSONArray monsters;
    public boolean waveComplete = false;
    public boolean preWavePauseComplete = false;
    public static final double SECONDS_BETWEEN_CHANGES = 0.01667;
    // Checking time every tenth of a second but keeps whole numbers by rounding times later
    private int timer = 0;

    public Wave(int duration, double pre_wave_pause, JSONArray monsters) {
        this.duration = (double)duration; // Needs to be double 
        this.durationLeft = duration;
        this.pre_wave_pause = pre_wave_pause;
        this.pre_wave_pauseLeft = pre_wave_pause;
        this.monsters = monsters;
    }

    public void tick() {
        this.timer++;
        if (timer == 1) {
            timer = 0;
            if (pre_wave_pauseLeft > 0) {
                pre_wave_pauseLeft -= SECONDS_BETWEEN_CHANGES;
            } else if (durationLeft > 0) {
                preWavePauseComplete = true;
                durationLeft -= SECONDS_BETWEEN_CHANGES;
            } else {
                waveComplete = true;
            }
        }

        /*
        // If more frames have passed than the number of seconds x the framerate
        // the circle jumps 30 pixels to the left
        if (this.timer > SECONDS_BETWEEN_CHANGES * App.FPS) { // this.timer > 6 (changes time every 10 frames = 1/10 of a second)
            if (pre_wave_pauseLeft > 0) {
                pre_wave_pauseLeft -= SECONDS_BETWEEN_CHANGES;
            } else if (durationLeft > 0) {
                durationLeft -= SECONDS_BETWEEN_CHANGES;
            } else {
                waveComplete = true;
            }
            this.timer = 0;
        }
        */
    }
    
    
    public boolean getWaveComplete() {
        return waveComplete;
    }

    public boolean getPreWavePauseComplete() {
        return preWavePauseComplete;
    }

    public int getDurationLeft() {
        return (int) durationLeft;
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
