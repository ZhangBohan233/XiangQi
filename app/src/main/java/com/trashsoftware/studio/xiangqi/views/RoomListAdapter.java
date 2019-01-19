package com.trashsoftware.studio.xiangqi.views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;


import com.trashsoftware.studio.xiangqi.LobbyActivity;
import com.trashsoftware.studio.xiangqi.MainActivity;
import com.trashsoftware.studio.xiangqi.R;
import com.trashsoftware.studio.xiangqi.program.HostGame;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder> {

    public ArrayList<HostGame> dataSet;

    private LobbyActivity activity;

    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView;
        private LobbyActivity activity;

        RoomViewHolder(@NonNull View itemView, LobbyActivity activity) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.memory_item_text);
            this.activity = activity;
        }

        void bindView(String text) {
            textView.setText(text);
        }

        @Override
        public void onClick(View v) {
//            String s = Util.parseDoubleString(textView.getText().toString());
//            activity.inputText(s);
//            activity.closeDrawer();
        }
    }

    public RoomListAdapter(LobbyActivity activity, ArrayList<HostGame> dataSet) {
        this.dataSet = dataSet;
        this.activity = activity;
    }

    public boolean removeItem(RoomViewHolder mvh) {
        String text = mvh.textView.getText().toString();
        return dataSet.remove(text);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.room_list_item, viewGroup, false);

        return new RoomViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder viewHolder, int i) {
        viewHolder.bindView(dataSet.get(i).toString());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
