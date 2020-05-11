package com.example.teamproject.CheckDoc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.teamproject.R;

import java.util.ArrayList;


public class CheckDetailRCAdapter extends RecyclerView.Adapter<CheckDetailRCAdapter.ViewHolder> {

    ArrayList<CheckDetailRCData> items = new ArrayList<>();

    Context context;

    String TAG = "CheckDetailRCAdapter";

    public CheckDetailRCAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더를 만드는 메소드, 뷰를 하나 만들고, 레이아웃 인플레이터를 통해 리사이클러뷰 아이템을 씌운다.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_check_detail_rc, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        CheckDetailRCData item = items.get(position);

        if(!item.imageURL.equals("")){
            // 글라이드 추가
            Glide.with(context).load(item.getImageURL()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageCell);
        }
    }

    // 이 밑으론 아이템을 추가하거나, 셋터 겟터
    public void addItem(CheckDetailRCData item){
        items.add(item);
    }

    public ArrayList<CheckDetailRCData> getItems() {
        return items;
    }

    public void setItems(ArrayList<CheckDetailRCData> items) {
        this.items = items;
    }

    public CheckDetailRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, CheckDetailRCData item){
        this.items.set(position, item);
    }


}
