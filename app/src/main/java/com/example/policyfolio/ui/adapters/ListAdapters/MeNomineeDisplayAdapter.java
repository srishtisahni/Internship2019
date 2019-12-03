package com.example.policyfolio.ui.adapters.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.R;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.data.local.classes.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MeNomineeDisplayAdapter extends RecyclerView.Adapter<MeNomineeDisplayAdapter.ViewHolder> {

    private Context context;
    private HashMap<String, User> users;
    private HashMap<String, ArrayList<Policy>> policies;
    private HashMap<Long, InsuranceProvider> providerHashMap;

    public MeNomineeDisplayAdapter(Context context, HashMap<String, User> users, HashMap<String, ArrayList<Policy>> policies, HashMap<Long, InsuranceProvider> providerHashMap){
        this.context = context;
        this.users = users;
        this.policies = policies;
        this.providerHashMap = providerHashMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.me_nominee_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<User> usersList = new ArrayList<>(users.values());
        User user = usersList.get(position);
        ArrayList<Policy> policyArrayList = policies.get(user.getId());

        holder.name.setText(user.getName());
        if(policyArrayList!=null) {
            Double totalCover = Policy.totalCover(policyArrayList);
            int cover = (int) Math.floor(totalCover);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String moneyString = formatter.format(cover);

            holder.amount.setText(moneyString.substring(0,moneyString.lastIndexOf(".00")));

            WhiteTextAdapter whiteTextAdapter = new WhiteTextAdapter(context,policyArrayList,providerHashMap);
            holder.policies.setAdapter(whiteTextAdapter);
            holder.policies.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        }
        else
            holder.amount.setText("");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView amount;
        RecyclerView policies;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
            policies = itemView.findViewById(R.id.policies);
        }
    }
}
