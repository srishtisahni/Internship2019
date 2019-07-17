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

import java.util.ArrayList;
import java.util.HashMap;

public class WhiteTextAdapter extends RecyclerView.Adapter<WhiteTextAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Policy> policies;
    private HashMap<Long, InsuranceProvider> providerHashMap;

    public WhiteTextAdapter(Context context, ArrayList<Policy> policies, HashMap<Long, InsuranceProvider> providerHashMap){
        this.context = context;
        this.policies = policies;
        this.providerHashMap = providerHashMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.text_white,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(policies.get(position).getPaid())
            holder.textView.setTextColor(context.getResources().getColor(R.color.white));
        else
            holder.textView.setTextColor(context.getResources().getColor(R.color.red));
        InsuranceProvider provider = providerHashMap.get(policies.get(position).getInsuranceProviderId());
        if(provider!=null)
            holder.textView.setText((position+1)+". "+provider.getName());
    }

    @Override
    public int getItemCount() {
        return policies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }
}
