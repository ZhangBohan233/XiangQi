package com.trashsoftware.studio.xiangqi.program;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.widget.EditText;

import com.trashsoftware.studio.xiangqi.LobbyActivity;
import com.trashsoftware.studio.xiangqi.views.WinDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static android.content.Context.WIFI_SERVICE;

public class HostGame {

//    public static final String mip="192.168.0.100";

    public static final int PORT = 3456;

    public static final byte START = 0;

    public static final byte CONFIRM = 1;

    public static final byte ASK_USERS = 2;

    public static final byte CLOSE = -1;

    private int roomId;

    private Player host;

    private Player guest;

    public static ServerSocket serverSocket;

    private Socket clientSocket;

    private String serverIP;

    public HostGame(Player host, final LobbyActivity parent) {
        this.host = host;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    WifiManager wm = W
//                    String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    serverSocket = new ServerSocket(PORT, 50, InetAddress.getLocalHost());
                    serverIP = serverSocket.getInetAddress().getHostAddress();
                    WinDialog dialog = new WinDialog();
                    dialog.show(parent.getSupportFragmentManager(), serverIP);
                    clientSocket = serverSocket.accept();
                } catch (IOException ioe) {
                    throw new RuntimeException();
                }
            }
        });

        thread.start();

    }

    public String getServerIP() {
        return serverIP;
    }

    @NonNull
    @Override
    public String toString() {
        return Integer.toString(roomId);
    }

    public static void clientListenToStart(Socket client, Runnable runInStart) {
        try {
            boolean start = false;
            byte[] buf = new byte[1024];
            InputStream is = client.getInputStream();

            int read;
            while (!start && (read = is.read(buf)) != -1) {
                if (read == 1) {
                    if (buf[0] == START) {
                        OutputStream os = client.getOutputStream();
                        os.write(new byte[]{CONFIRM});
                        os.flush();

                        start = true;
                        runInStart.run();
                    }
                } else {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
