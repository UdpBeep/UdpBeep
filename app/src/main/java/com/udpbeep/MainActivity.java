package com.udpbeep;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.udpbeep.F3FChrono.Mode.Practice;
import static com.udpbeep.F3FChrono.Mode.Test;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    TextView infoIp, infoPort;
    TextView textViewState;
    TextView textCounter;

    int TimerId=0;

    static final int udpServerPORT = 4445;
    UdpServerThread udpServerThread;
    static F3FChrono chrono;
    static int base =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        infoIp = (TextView) findViewById(R.id.infoip);
        infoPort = (TextView) findViewById(R.id.infoport);
        textViewState = (TextView)findViewById(R.id.state);
        textCounter = (TextView)findViewById(R.id.Counter);

        infoIp.setText(getIpAddress());
        infoPort.setText(String.valueOf(udpServerPORT));
        textCounter.setText("");

        chrono = new F3FChrono();
        chrono.create(Test);
        chrono.start(Practice);

        textCounter.setTextSize((float)60.0);


    }

    @Override
    protected void onStart() {
        udpServerThread = new UdpServerThread(udpServerPORT);
        udpServerThread.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(udpServerThread != null){
            udpServerThread.setRunning(false);
            udpServerThread = null;
        }

        super.onStop();
    }

    private void updateState(final String state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewState.setText(state);
            }
        });
    }

    public void BtnBase1OnClick(View view) {
        if(chrono.declareBase(1)) {
            updateTextCounter(chrono.getLapCount(), chrono.getLastLapTime(),
                    chrono.getLast10BasesTime(), chrono.getLast10BasesLostTime());
        }
        base++;
    }

    public void BtnBase2OnClick(View view) {
        if(chrono.declareBase(2)) {
            updateTextCounter(chrono.getLapCount(), chrono.getLastLapTime(),
                    chrono.getLast10BasesTime(), chrono.getLast10BasesLostTime());
        }
        base++;
    }

    public void resetButton(View view) {
        chrono.reset();
        updateTextCounter(chrono.getLapCount(), chrono.getLastLapTime(),
                chrono.getLast10BasesTime(), chrono.getLast10BasesLostTime());
    }

    public void startRace(View view) {
        chrono.startRace();
        updateTextCounter(chrono.getLapCount(), chrono.getLastLapTime(),
                chrono.getLast10BasesTime(), chrono.getLast10BasesLostTime());
    }

    public void updateTextCounter(final int count, final double time, final double runTime,
                                  final double lostTime) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //textCounter.setText(String.format("%d: %10.3f ; 10 laps : %10.3f; lost : %10.3f \n%s",
                //        count, time, runTime, lostTime, textCounter.getText()));
                if (chrono.isInStart()) {
                    textCounter.setText("In Start");
                }
                else {
                    textCounter.setText(String.format("%10.3f \n %10.3f  \n %d/%d",runTime,lostTime,min(10, count),count));
                }



                //System.out.println(String.format("%d: %10.3f ; run : %10.3f ; lost : %10.3f",
                //        count, time, runTime, lostTime));
            }
        });
    }

    private class UdpServerThread extends Thread{

        int serverPort;
        byte[] IpAddress;
        DatagramSocket socket;

        boolean running;

        public UdpServerThread(int serverPort) {
            super();
            this.serverPort = serverPort;
        }

        public void setRunning(boolean running){
            this.running = running;
        }

        @Override
        public void run() {

            running = true;

            try {
                updateState("Starting UDP Server");
                socket = new DatagramSocket(serverPort);

                updateState("UDP Server is running");
                Log.e(TAG, "UDP Server is running");

                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);

                while(running){
                    byte[] buf = new byte[256];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);     //this code block the program flow

					toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); // 200 is duration in ms
                    IpAddress = packet.getAddress().getAddress();
                    if(chrono.declareBase(IpAddress[3])) {
                        updateTextCounter(chrono.getLapCount(), chrono.getLastLapTime(),
                                chrono.getLast10BasesTime(), chrono.getLast10BasesLostTime());
                    }
                }
                Log.e(TAG, "UDP Server ended");

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(socket != null){
                    socket.close();
                    Log.e(TAG, "socket.close()");
                }
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}
