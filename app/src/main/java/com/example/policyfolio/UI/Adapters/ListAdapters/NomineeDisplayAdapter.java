package com.example.policyfolio.UI.Adapters.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.R;
import com.example.policyfolio.Data.Local.Classes.Nominee;

import java.util.ArrayList;

public class NomineeDisplayAdapter extends RecyclerView.Adapter<NomineeDisplayAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Nominee> nominees;
    private String[] relation;

    public NomineeDisplayAdapter(Context context, ArrayList<Nominee> nominees){
        this.context = context;
        this.nominees = nominees;
        relation = context.getResources().getStringArray(R.array.relationship_array);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nominee_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(nominees.get(position).getName());
        holder.phone.setText(nominees.get(position).getPhone());
        holder.email.setText(nominees.get(position).getEmail());
        holder.relation.setText(relation[nominees.get(position).getRelation()]);
    }

    @Override
    public int getItemCount() {
        return nominees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView relation;
        TextView phone;
        TextView email;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            relation = itemView.findViewById(R.id.relation);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            image = itemView.findViewById(R.id.image);
        }
    }
}
