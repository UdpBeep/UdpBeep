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

import static com.udpbeep.F3FChrono.Mode.Test;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    TextView infoIp, infoPort;
    TextView textViewState, textViewPrompt;
    TextView TextCounter;

    int TimerId=0;

    static final int UdpServerPORT = 4445;
    static final int NbSecond = 0;
    UdpServerThread udpServerThread;
    static F3FChrono Chrono;
    static int Base=0;
    static double BaseTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        infoIp = (TextView) findViewById(R.id.infoip);
        infoPort = (TextView) findViewById(R.id.infoport);
        textViewState = (TextView)findViewById(R.id.state);
        textViewPrompt = (TextView)findViewById(R.id.prompt);
        TextCounter = (TextView)findViewById(R.id.Counter);

        infoIp.setText(getIpAddress());
        infoPort.setText(String.valueOf(UdpServerPORT));
        TextCounter.setText("");

        Chrono = new F3FChrono();
        Chrono.Create(Test);


    }

    @Override
    protected void onStart() {
        udpServerThread = new UdpServerThread(UdpServerPORT);
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

    private void updatePrompt(final String prompt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewPrompt.append(prompt);
            }
        });
    }

    public void BtnBase1OnClick(View view) {
        BaseTime = Chrono.DeclareBase(1);
        updateTextCounter(BaseTime);
        Base++;
    }

    public void BtnBase2OnClick(View view) {
        BaseTime = Chrono.DeclareBase(2);
        updateTextCounter(BaseTime);
        Base++;
    }

    public void updateTextCounter(final double time) {
        if (BaseTime>=0) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextCounter.setText(String.format("%10.3f \t\t%s",time, TextCounter.getText()));
                }
            });
        }
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

                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 750);

                while(running){
                    byte[] buf = new byte[256];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);     //this code block the program flow

					toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); // 200 is duration in ms
                    IpAddress = packet.getAddress().getAddress();
                    BaseTime = Chrono.DeclareBase(IpAddress[3]);
                    updateTextCounter(BaseTime);
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
