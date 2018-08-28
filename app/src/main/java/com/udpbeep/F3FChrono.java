package com.udpbeep;

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

    private long ChronoLaunch, ChronoFirstBase, ChronoRun;
    private int Lap;
    private double ChronoLap[]=new double[10000];
    private Mode pmode;
    //private ChronoThread ChronoThreadVar;
    private int LastBase=-1;
    long ChronoTime;

    //Init class F3F Chrono
    public int Create (Mode mode){

        switch(mode){
            case Test:
                ChronoLaunch = -1;
                ChronoFirstBase = -1;
                ChronoRun = -1;
                break;
            case Run:
                ChronoLaunch = CHRONOLAUNCHTIME;
                ChronoFirstBase = CHRONOFIRSTBASE;
                ChronoRun = 0;
                //ChronoThreadVar = new ChronoThread();
               break;
            case Practice:
                ChronoLaunch = -1;
                ChronoFirstBase = -1;
                ChronoRun = 0;
                break;
            default:
                ChronoLaunch = -1;
                ChronoFirstBase = -1;
                ChronoRun = -1;
                break;
        }
        pmode=mode;
        ChronoTime=System.currentTimeMillis();

        System.out.println("F3F Initialisation " + mode);
        return 0;
    }

    public int Start (Mode mode){
        ChronoTime=System.currentTimeMillis();
        return 0;
    }

    public double DeclareBase (int base){
        if (base!=this.LastBase){
            this.ChronoLap[this.Lap]= (double) ((System.currentTimeMillis()-ChronoTime)/1000.00);
            ChronoTime=System.currentTimeMillis();
            this.Lap++;
            this.LastBase=base;
        }else{
            //Base declaration is the same
            this.ChronoLap[this.Lap]= -1.0;
            this.Lap++;

        }
        return (this.ChronoLap[this.Lap-1]);
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
