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
import java.util.ArrayList;

// TODO: consider following https://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-type to implement multiple views ?
public class DisplayPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private JSONArray sections, access;
    private ArrayList<String> accessArrayList;
    private String username, docOwner;

    private OnNoteListener mOnNoteListener;

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionTitle, documentSection;
        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
            documentSection = (TextView) itemView.findViewById(R.id.documentSection);
        }
    }

    public static class HiddenSectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView hiddenSection;
        OnNoteListener onNoteListener;
        public HiddenSectionViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            hiddenSection = (TextView) itemView.findViewById(R.id.hiddenSection);
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

    // ONLY FOR HIDDENSECTIONVIEWHOLDER
    public interface OnNoteListener {
        void onNoteClick(int position) throws JSONException, IOException;
    }

    public DisplayPageAdapter(JSONArray sections, JSONArray access, String docID, String username, String docOwner, OnNoteListener onNoteListener) throws JSONException {
        this.sections = sections;
        this.access = access;
        this.mOnNoteListener = onNoteListener;
        this.username = username;
        this.docOwner = docOwner;

        this.accessArrayList = new ArrayList<String>();
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < this.access.length(); i++) {
            temp.add(this.access.getJSONObject(i).getString("id"));
        }
        if (temp.contains(docID)) {
            int tempIndex = temp.indexOf(docID);
            for (int i = 0; i < this.access.getJSONObject(tempIndex).getJSONArray("section").length(); i++) {
                this.accessArrayList.add(this.access.getJSONObject(tempIndex).getJSONArray("section").getString(i));
            }
        } else {
            this.accessArrayList.clear();
        }
    }

    @Override // if the user has access, return SectionViewHolder; else, HiddenSectionViewHolder
    public int getItemViewType(int position) {
        // TODO: IMPLEMENT
        if (username.equals(docOwner)) return 0;
        if (this.accessArrayList.isEmpty()) return 1;
        return this.accessArrayList.contains(Integer.toString(position)) ? 0: 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType) {
            case 0: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_layout, parent, false);
                return new SectionViewHolder(view);
            }
            case 1: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hidden_section_layout, parent, false);
                return new HiddenSectionViewHolder(view, mOnNoteListener);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
                try {
                    sectionViewHolder.sectionTitle.setText(sections.getJSONObject(position).getString("section_title"));
                    sectionViewHolder.documentSection.setText(sections.getJSONObject(position).getString("section_content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
                HiddenSectionViewHolder hiddenSectionViewHolder = (HiddenSectionViewHolder) holder;
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return sections.length();
    }
}
