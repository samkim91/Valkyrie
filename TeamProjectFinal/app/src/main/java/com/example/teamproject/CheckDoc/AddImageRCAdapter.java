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
import com.example.teamproject.R;

import java.util.ArrayList;


public class AddImageRCAdapter extends RecyclerView.Adapter<AddImageRCAdapter.ViewHolder> implements OnAddImageRCClickListener {

    ArrayList<AddImageRCData> items = new ArrayList<>();

    Context context;

    OnAddImageRCClickListener listener;

    String TAG = "AddImageRCAdapter";

    public AddImageRCAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더를 만드는 메소드, 뷰를 하나 만들고, 레이아웃 인플레이터를 통해 리사이클러뷰 아이템을 씌운다.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_add_doc_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        AddImageRCData item = items.get(position);

        if(!item.imageURL.equals("")){
            // 글라이드 추가
            Glide.with(context).load(item.getImageURL()).into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 아이템 클릭 리스너를 달기 위한 메소드
    public void setOnItemClickListener(OnAddImageRCClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, View view, int position) {
        if(listener != null){
            listener.onItemClick(viewHolder, view, position);
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageCell);
            cancel = itemView.findViewById(R.id.close_btn);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }
    }

    // 이 밑으론 아이템을 추가하거나, 셋터 겟터
    public void addItem(AddImageRCData item){
        items.add(item);
    }

    public ArrayList<AddImageRCData> getItems() {
        return items;
    }

    public void setItems(ArrayList<AddImageRCData> items) {
        this.items = items;
    }

    public AddImageRCData getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, AddImageRCData item){
        this.items.set(position, item);
    }


}
