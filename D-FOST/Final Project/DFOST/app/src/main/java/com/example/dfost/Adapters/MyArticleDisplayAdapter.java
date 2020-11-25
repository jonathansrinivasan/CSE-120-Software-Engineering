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

public class MyArticleDisplayAdapter extends RecyclerView.Adapter<MyArticleDisplayAdapter.ViewHolder>{
    private JSONArray sections;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionTitle, documentSection;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
            documentSection = (TextView) itemView.findViewById(R.id.documentSection);
        }
    }

    public MyArticleDisplayAdapter(JSONArray sections) {
        this.sections = sections;
    }

    @NonNull
    @Override
    public MyArticleDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyArticleDisplayAdapter.ViewHolder holder, int position) {
        try {
            holder.sectionTitle.setText(sections.getJSONObject(position).getString("section_title"));
            holder.documentSection.setText(sections.getJSONObject(position).getString("section_content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return sections.length();
    }
}
