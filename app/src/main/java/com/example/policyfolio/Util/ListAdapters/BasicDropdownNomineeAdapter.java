package com.example.policyfolio.Util.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.R;

import java.util.ArrayList;

public class BasicDropdownNomineeAdapter extends RecyclerView.Adapter<BasicDropdownNomineeAdapter.Holder> {

    private Context context;
    private ArrayList<Nominee> values;
    private ParentCallback callback;

    public BasicDropdownNomineeAdapter(Context context, ArrayList<Nominee> values, ParentCallback callback){
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
        String[] relations = context.getResources().getStringArray(R.array.relationship_array);
        holder.textView.setText(values.get(position).getName() + ", " + relations[values.get(position).getRelation()]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setValue(position, Constants.DropDownType.NOMINEE);
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
