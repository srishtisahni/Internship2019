package com.example.policyfolio.Util.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.R;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Util.Constants;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class YellowTextAdapter extends RecyclerView.Adapter<YellowTextAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Policy> policies;
    private HashMap<Long, InsuranceProvider> providerHashMap;
    private int type;

    public YellowTextAdapter(Context context, ArrayList<Policy> policies, HashMap<Long, InsuranceProvider> providerHashMap,int type){
        this.context = context;
        this.policies = policies;
        this.providerHashMap = providerHashMap;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.text_yellow,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InsuranceProvider provider = providerHashMap.get(policies.get(position).getInsuranceProviderId());
        if(provider!=null) {
            if(type==Constants.Policy.DISPLAY_PREMIUM) {
                holder.textView.setText("- " + Constants.DATE_FORMAT.format(new Date(policies.get(position).getNextDueDate())) + " | " + provider.getName());
                holder.amount.setText(policies.get(position).getPremium());
            }
            else {
                holder.textView.setText("- " + Constants.DATE_FORMAT.format(new Date(policies.get(position).getMatureDate())) + " | " + provider.getName());
                holder.amount.setText(policies.get(position).getSumAssured());
            }
        }
    }

    @Override
    public int getItemCount() {
        return policies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
