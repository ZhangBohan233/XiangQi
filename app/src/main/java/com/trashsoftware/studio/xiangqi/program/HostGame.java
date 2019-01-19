package com.trashsoftware.studio.xiangqi.program;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HostGame {

//    public static final String mip="192.168.0.100";

    private int roomId;

    private Player host;

    private Player guest;

    public static ServerSocket serverSocket;

    private Socket clientSocket;

    public HostGame(final int id, Player host) {
        this.roomId = id;
        this.host = host;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(id);

                    clientSocket = serverSocket.accept();
                } catch (IOException ioe) {
                    throw new RuntimeException();
                }
            }
        });

        thread.start();

    }

    @NonNull
    @Override
    public String toString() {
        return Integer.toString(roomId);
    }
}
