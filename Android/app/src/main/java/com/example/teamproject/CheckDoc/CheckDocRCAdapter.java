package com.example.teamproject.CheckDoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamproject.R;

import java.util.ArrayList;

public class CheckDocRCAdapter extends RecyclerView.Adapter<CheckDocRCAdapter.ViewHolder> implements OnCheckDocRCClickListener {

    ArrayList<CheckDocRCData> items = new ArrayList<>();

    OnCheckDocRCClickListener listener;
    Context context;

    public CheckDocRCAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_check_doc_history2, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckDocRCData item = items.get(position);

        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnCheckDocRCClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, View view, int position) {
        if(listener != null){
            listener.onItemClick(viewHolder, view, position);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, docInfo, time, writer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            docInfo = itemView.findViewById(R.id.docInfo);
            time = itemView.findViewById(R.id.time);
            writer = itemView.findViewById(R.id.writer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

        }

        public void setItem(CheckDocRCData item){

            title.setText(item.getTitle());
            docInfo.setText(item.getDocInfo());
            time.setText(item.getTime());
            writer.setText(item.getWriter());

        }
    }

    public void addItem(CheckDocRCData item){
        items.add(item);
    }

    public ArrayList<CheckDocRCData> getItems(){
        return items;
    }

    public void setItems(ArrayList<CheckDocRCData> items){
        this.items = items;
    }

    public CheckDocRCData getItem(int position){
        return items.get(position);
    }

    private void setItem(int position, CheckDocRCData item){
        this.items.set(position, item);
    }
}
