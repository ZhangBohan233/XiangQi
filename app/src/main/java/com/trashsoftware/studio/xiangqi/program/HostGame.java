package com.trashsoftware.studio.xiangqi.program;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.widget.EditText;

import com.trashsoftware.studio.xiangqi.LobbyActivity;
import com.trashsoftware.studio.xiangqi.views.WinDialog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static android.content.Context.WIFI_SERVICE;

public class HostGame {

//    public static final String mip="192.168.0.100";

    public final static int PORT = 3456;

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
}
