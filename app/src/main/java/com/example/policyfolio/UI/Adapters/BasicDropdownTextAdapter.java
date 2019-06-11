package com.example.policyfolio.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.Constants;
import com.example.policyfolio.R;

public class BasicDropdownTextAdapter extends RecyclerView.Adapter<BasicDropdownTextAdapter.Holder> {

    private Context context;
    private String[] values;
    private ParentCallback callback;
    private int type;

    public BasicDropdownTextAdapter(Context context, String[] values, ParentCallback callback, int type){
        this.context = context;
        this.values = values;
        this.callback = callback;
        this.type = type;
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
        holder.textView.setText(values[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setValue(position, type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.length;
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
