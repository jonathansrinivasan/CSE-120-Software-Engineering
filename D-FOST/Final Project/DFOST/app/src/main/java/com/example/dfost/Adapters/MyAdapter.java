package com.example.dfost.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dfost.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private JSONArray jAR;

    private OnNoteListener mOnNoteListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title, author, date;
        OnNoteListener onNoteListener;
        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.docTitleView);
            author = (TextView) itemView.findViewById(R.id.authorView);
            date = (TextView) itemView.findViewById(R.id.dateView);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                onNoteListener.onNoteClick(getAdapterPosition());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position) throws JSONException, IOException;
    }

    public MyAdapter(JSONArray jAR, OnNoteListener onNoteListener) {
        this.jAR = jAR;this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        try {
            JSONObject json = jAR.getJSONObject(position).getJSONObject("header");
            holder.title.setText(json.getString("title"));
            holder.author.setText(json.getString("username"));
            holder.date.setText(json.get("date").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jAR.length();
    }
}
