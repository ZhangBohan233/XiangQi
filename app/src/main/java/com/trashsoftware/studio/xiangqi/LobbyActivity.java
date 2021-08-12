package com.trashsoftware.studio.xiangqi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trashsoftware.studio.xiangqi.connection.GameConnection;
import com.trashsoftware.studio.xiangqi.connection.Host;
import com.trashsoftware.studio.xiangqi.program.Player;
import com.trashsoftware.studio.xiangqi.views.WinDialog;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LobbyActivity extends AppCompatActivity {

//    RoomListAdapter adapter;

    private EditText ipText;

    private TextView messageText;

    private Button startButton;

    private GameConnection gameConnection;

    private static InputStream inputStream;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        ipText = findViewById(R.id.port_text);
        messageText = findViewById(R.id.message_text);
        startButton = findViewById(R.id.start_button);

//        initRecyclerView(new ArrayList<HostGame>());
    }

    public void createRoomAction(View view) {
        try {
            String localHostAddress = ipText.getText().toString();
            gameConnection = new GameConnection(localHostAddress, 1);
            gameConnection.createServer();

            messageText.setText(R.string.create_success);
//            startButton.setEnabled(true);

            System.out.println("Listening on ip " + localHostAddress);

//            refreshListOwner();

            Thread listen = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!gameConnection.isFull()) {
                            gameConnection.acceptOne();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startButton.setEnabled(true);
                                messageText.setText(R.string.ready);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            listen.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void getHostIpAction(View view) {
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String ip = Host.getHost().getHostAddress();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ipText.setText(ip);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        get.start();
    }

    public static InputStream getInputStream() {
        return inputStream;
    }

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    public void joinGameAction(View view) {

        final String ip = ipText.getText().toString();

        Thread listening = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket client = new Socket();
                    System.out.println("Trying connecting to " + ip);
                    client.connect(new InetSocketAddress(ip, GameConnection.PORT));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageText.setText(R.string.connect_success);
                        }
                    });
                    System.out.println("Connected to " + ip);

                    GameConnection.clientListenToStart(client, new Runnable() {
                        @Override
                        public void run() {
                            startGame(client, false);
                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageText.setText(R.string.connect_failed);
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
        listening.start();

    }

    public void startGameAction(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gameConnection.broadcastStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        startGame(gameConnection.getClientSockets()[0], true);
    }

    private void startGame(Socket client, boolean isServer) {
        try {
            inputStream = client.getInputStream();
            outputStream = client.getOutputStream();

            Intent intent = new Intent(this, GameActivity.class);

            intent.putExtra("isServer", isServer);
            intent.putExtra("isLocal", false);

            startActivity(intent);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    private void showDialog2(int code) {
        WinDialog dialog = new WinDialog();
        dialog.show(getSupportFragmentManager(), getString(code));
    }
}
