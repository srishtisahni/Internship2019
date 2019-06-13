package com.example.policyfolio.Util.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.R;

import java.util.ArrayList;

public class BasicDropdownProviderAdapter extends RecyclerView.Adapter<BasicDropdownProviderAdapter.Holder> {

    private Context context;
    private ArrayList<InsuranceProvider> values;
    private ParentCallback callback;

    public BasicDropdownProviderAdapter(Context context, ArrayList<InsuranceProvider> values, ParentCallback callback){
        this.context = context;
        this.values = values;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.dropdown_add_fragment,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.textView.setText(values.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setValue(position, Constants.DropDownType.INSURANCE_PROVIDER);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        View itemView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            this.itemView = itemView;
        }
    }

    public interface ParentCallback {
        void setValue(int position, int type);
    }
}
