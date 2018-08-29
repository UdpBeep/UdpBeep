package com.udpbeep;

import java.util.ArrayList;

//sda F3FChronoClass
public class F3FChrono {

    public static final int CHRONOLAUNCHTIME = 30000;
    public static final int CHRONOFIRSTBASE = 30000;


    public enum Mode{
        Test,
        Run,
        Practice;
    }

    public enum Status{
        Launch,
        FirstBase,
        Run,
        Finish;
    }

    private long chronoLaunch, chronoFirstBase, chronoRun;
    private int lap;
    private ArrayList<Double> chronoLap =new ArrayList<Double>();
    private ArrayList<Double> timeLoss=new ArrayList<Double>();
    private double last10BasesTime;
    private double last10BasesTimeLoss;
    private Mode pmode;
    //private ChronoThread ChronoThreadVar;
    private int lastBase =-1;
    long lastBaseChangeTime;
    long lastDetectionTime;

    //Init class F3F Chrono
    public int create(Mode mode){

        switch(mode){
            case Test:
                chronoLaunch = -1;
                chronoFirstBase = -1;
                chronoRun = -1;
                break;
            case Run:
                chronoLaunch = CHRONOLAUNCHTIME;
                chronoFirstBase = CHRONOFIRSTBASE;
                chronoRun = 0;
                //ChronoThreadVar = new ChronoThread();
               break;
            case Practice:
                chronoLaunch = -1;
                chronoFirstBase = -1;
                chronoRun = 0;
                break;
            default:
                chronoLaunch = -1;
                chronoFirstBase = -1;
                chronoRun = -1;
                break;
        }
        pmode=mode;
        last10BasesTime = 0.0;
        last10BasesTimeLoss = 0.0;

        System.out.println("F3F Initialisation " + mode);
        return 0;
    }

    public int start(Mode mode){
        lastBaseChangeTime =System.currentTimeMillis();
        lastDetectionTime = lastBaseChangeTime;
        return 0;
    }

    public boolean declareBase(int base){
        long now = System.currentTimeMillis();
        if (base!=this.lastBase){
            double elapsedTime = (double) ((now- lastBaseChangeTime)/1000.00);
            lastBaseChangeTime = now;
            lastDetectionTime = now;
            if (getLapCount()>1) {
                last10BasesTimeLoss += timeLoss.get(getLapCount() - 1);
            }
            chronoLap.add(elapsedTime);
            timeLoss.add(0.0);
            last10BasesTime+=elapsedTime;
            if (getLapCount()>10) {
                last10BasesTime-= chronoLap.get(getLapCount()-11);
                last10BasesTimeLoss-=timeLoss.get(getLapCount()-11);
            }
            lastBase =base;
            return true;
        }else{
            //Base declaration is the same
            double elapsedTime = (double) ((now- lastDetectionTime)/1000.00);
            lastDetectionTime = now;
            timeLoss.set(getLapCount()-1,timeLoss.get(getLapCount()-1)+elapsedTime);
            return false;
        }
    }

    public double getLast10BasesTime () {
        return last10BasesTime;
    }

    public double getLast10BasesLostTime () {
        return last10BasesTimeLoss;
    }

    public double getLastLapTime() {
        return chronoLap.get(getLapCount()-1);
    }

    public int getLapCount() {
        return chronoLap.size();
    }


 /*   private class ChronoThread extends Thread{

        long Chrono;
        Calendar c=Calendar.getInstance(Locale.FRANCE,);
        c.;
        boolean running;

        public ChronoThread() {
            super();
            this.Chrono= System.currentTimeMillis() ;
        }
*/
}
