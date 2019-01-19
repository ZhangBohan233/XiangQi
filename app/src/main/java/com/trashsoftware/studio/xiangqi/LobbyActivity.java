package com.trashsoftware.studio.xiangqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.trashsoftware.studio.xiangqi.program.HostGame;
import com.trashsoftware.studio.xiangqi.program.Player;
import com.trashsoftware.studio.xiangqi.views.RoomListAdapter;
import com.trashsoftware.studio.xiangqi.views.WinDialog;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class LobbyActivity extends AppCompatActivity {

//    RoomListAdapter adapter;

    private EditText portText;

    String mip = "127.0.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        portText = findViewById(R.id.port_text);

//        initRecyclerView(new ArrayList<HostGame>());
    }

    public void createRoomAction(View view) {
        HostGame hostGame = new HostGame(getPort(), new Player());
//        adapter.dataSet.add(hostGame);
//        adapter.notifyItemInserted(adapter.dataSet.size() - 1);
    }

    public void joinGameAction(View view) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(HostGame.serverSocket.getInetAddress().getHostAddress(), getPort());

                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    showDialog2(R.string.app_name);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    showDialog2(R.string.no_port);
                }
            }
        });
        thread.start();
    }


    private int getPort() {
        return Integer.valueOf(portText.getText().toString());
    }
//    private void initRecyclerView(ArrayList<HostGame> strings) {
//        RecyclerView rv = findViewById(R.id.room_list);
//        rv.setLayoutManager(new GridLayoutManager(this, 1));
//        adapter = new RoomListAdapter(this, strings);
//        rv.setAdapter(adapter);
//    }

    private void showDialog2(int code) {
        WinDialog dialog = new WinDialog();
        dialog.show(getSupportFragmentManager(), getString(code));
    }
}
