package com.example.policyfolio.ui.adapters.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.R;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.util.Constants;

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
        this.policies = new ArrayList<>();
        if(type == Constants.Policy.DISPLAY_PREMIUM) {
            for (int i = 0; i < policies.size(); i++)
                if ((!policies.get(i).getPaid()) || (policies.get(i).getNextDueDate() <= (System.currentTimeMillis() / 1000 + Constants.Time.EPOCH_WEEK * 2)))
                    this.policies.add(policies.get(i));
        }
        else {
            for (int i = 0; i < policies.size(); i++)
                if ((!policies.get(i).getPaid()) || (policies.get(i).getMatureDate() <= (System.currentTimeMillis() / 1000 + Constants.Time.EPOCH_YEAR)))
                    this.policies.add(policies.get(i));
        }
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
        if(policies.get(position).getPaid()){
            holder.textView.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.amount.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        else {
            holder.textView.setTextColor(context.getResources().getColor(R.color.red));
            holder.amount.setTextColor(context.getResources().getColor(R.color.red));
        }
        if(provider!=null) {
            if(type==Constants.Policy.DISPLAY_PREMIUM) {
                holder.textView.setText("- " + Constants.Time.DATE_FORMAT.format(new Date(policies.get(position).getNextDueDate()*1000)) + " | " + provider.getName());
                holder.amount.setText(policies.get(position).getPremium());
            }
            else {
                holder.textView.setText("- " + Constants.Time.DATE_FORMAT.format(new Date(policies.get(position).getMatureDate()*1000)) + " | " + provider.getName());
                holder.amount.setText(policies.get(position).getSumAssured());
            }
        }
    }

    @Override
    public int getItemCount() {
        return policies.size();
    }

    public void updatePolicies(ArrayList<Policy> policies){
        this.policies.clear();
        if(type == Constants.Policy.DISPLAY_PREMIUM) {
            for (int i = 0; i < policies.size(); i++)
                if ((!policies.get(i).getPaid()) || (policies.get(i).getNextDueDate() <= (System.currentTimeMillis() / 1000 + Constants.Time.EPOCH_WEEK * 2)))
                    this.policies.add(policies.get(i));
        }
        else {
            for (int i = 0; i < policies.size(); i++)
                if ((!policies.get(i).getPaid()) || (policies.get(i).getMatureDate() <= (System.currentTimeMillis() / 1000 + Constants.Time.EPOCH_YEAR)))
                    this.policies.add(policies.get(i));
        }
        notifyDataSetChanged();
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
