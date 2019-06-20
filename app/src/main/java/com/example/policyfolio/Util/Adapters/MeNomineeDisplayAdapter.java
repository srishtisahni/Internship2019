package com.example.policyfolio.Util.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.R;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;

import java.util.ArrayList;
import java.util.HashMap;


public class MeNomineeDisplayAdapter extends RecyclerView.Adapter<MeNomineeDisplayAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> user;
    private HashMap<String, ArrayList<Policy>> policies;

    public MeNomineeDisplayAdapter(Context context,ArrayList<User> user,HashMap<String, ArrayList<Policy>> policies){
        this.context = context;
        this.user = user;
        this.policies = policies;
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

    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
