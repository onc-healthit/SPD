package com.esacinc.spd.util;


import java.util.Timer;
import java.util.TimerTask;

/**
 * This class manages a timer that may or may not output a "." character
 * to the system console as the merge process proceeds. Upon terminating the timer,
 * the elapsed time that the timer ran is returned (truncated to seconds)
 * 
 * @author Dan Donahue ESAC Inc.
 *
 */
public class ProgressTimer {

    static int counter = 0;
    boolean doOutput = false;
    long startTime = 0;
    long endTime = 0;
    Timer timer = null;
    
    /**
     * Constructor for the timer. The boolean argument controls whether a
     * "." character is output to the system console every time the timer fires.
     * 
     * @param showOutput  true: output "." character to system console. 
     *                    false: no output generated.
     */
    public ProgressTimer(boolean showOutput) {
    	doOutput = showOutput;
    	startTime = System.currentTimeMillis();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (doOutput) System.out.print(".");
                counter++;//increments the counter
            }
        };
        if (doOutput) System.out.print("    ");
        timer = new Timer("ProgressTimer");//create a new Timer

        timer.scheduleAtFixedRate(timerTask, 30, 1000);//this line starts the timer at the same time its executed
    }
    
    /**
     * Cancels the timer if it is running, and returns the elapsed time (in seconds) that the timer has run.
     * 
     * @return elapsed runtime of the timer, in seconds.
     */
    public long stop() {
    	endTime = System.currentTimeMillis();
    	if (timer != null) {
    		timer.cancel();
    	}
    	if (doOutput) System.out.println(".");
    	return (endTime - startTime)/1000;
    }
    
    /**
     * Convenience method to return a time in seconds represented as hours, minutes, seconds
     * 
     * @param secs
     * @return string
     */
    public String convertToHours(long secs) {
    	String hrsSecs = "";
    	int hours = (int)(secs / 3600);
        int minutes = (int)((secs%3600)/60);
        int seconds = (int)((secs% 3600)%60);
    	hrsSecs = String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    	return hrsSecs;
    }
}
