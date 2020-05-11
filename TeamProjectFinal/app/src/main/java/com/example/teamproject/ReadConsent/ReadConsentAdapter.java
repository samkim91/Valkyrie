package com.example.teamproject.ReadConsent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamproject.R;

import java.util.ArrayList;

public class ReadConsentAdapter extends RecyclerView.Adapter<ReadConsentAdapter.CustomViewHolder> {

    private ArrayList<ConsentData> mList = null;
    private Context context = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListenerOnClick = null;
    private OnItemClickListener mListenerLongClick = null;
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListenerOnClick = listener;
    }
    public void setOnItemClickListenerLong(OnItemClickListener listener) {
        this.mListenerLongClick = listener;
    }

    public ReadConsentAdapter(Activity context, ArrayList<ConsentData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView read_docName, read_patName, read_surgeryName, read_time;

        public CustomViewHolder(View view) {
            super(view);
            read_docName = view.findViewById(R.id.read_docName);
            read_patName = view.findViewById(R.id.read_patName);
            read_surgeryName = view.findViewById(R.id.read_surgeryName);
            read_time = view.findViewById(R.id.read_time);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListenerOnClick != null) {
                            mListenerOnClick.onItemClick(v, pos);
                        }
                    }

                }
            });


        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_consent, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        context = viewGroup.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        String time = mList.get(position).getTime();
        String timeEdited = time.substring(0,4)+"."+time.substring(4,6)+"."+time.substring(6,8);

        viewholder.read_docName.setText(mList.get(position).getDocName());
        viewholder.read_patName.setText(mList.get(position).getPatName());
        viewholder.read_surgeryName.setText(mList.get(position).getPapaername());
        viewholder.read_time.setText(timeEdited);


    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
