package com.example.dfost.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dfost.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class CreateDocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public ArrayList<JSONObject> sectionInfo;

    class ViewHolder0 extends RecyclerView.ViewHolder { // section stuff
        private EditText sectionTitle, sectionContent;
        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            sectionTitle = (EditText) itemView.findViewById(R.id.sectionTitle);
            sectionContent = (EditText) itemView.findViewById(R.id.sectionContent);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder { // no section stuff
        private TextView noSection;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            noSection = (TextView) itemView.findViewById(R.id.noSections);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return sectionInfo.isEmpty() ? 1: 0;
    }

    public CreateDocumentAdapter(ArrayList<JSONObject> sectionInfo) {
        this.sectionInfo = sectionInfo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_doc_section_box, parent, false);
                return new ViewHolder0(view);
            }
            case 1: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_sections_layout, parent, false);
                return new ViewHolder1(view);
            }

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                ViewHolder0 viewHolder0 = (ViewHolder0) holder;

                break;
            }
            case 1: {
                ViewHolder1 viewHolder1 = (ViewHolder1) holder;

                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return sectionInfo.size();
        //return sectionInfo.isEmpty() ? 1: sectionInfo.size();
    }


}
