package com.example.policyfolio.ui.adapters.ListAdapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.policyfolio.R;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Policy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class PolicyDisplayAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;

    private ArrayList<ArrayList<Policy>> typeWisePolicy;
    private String[] titles;
    private Drawable[] icons;

    private ParentCallback callback;
    private HashMap<Long, InsuranceProvider> providersHashMap;

    public PolicyDisplayAdapter(Context context, ArrayList<ArrayList<Policy>> typeWisePolicy, HashMap<Long, InsuranceProvider> providerHashMap, ParentCallback callack){
        this.context = context;
        this.typeWisePolicy = typeWisePolicy;
        this.titles = context.getResources().getStringArray(R.array.insurance_type);
        TypedArray drawable = context.getResources().obtainTypedArray(R.array.icons);
        icons = new Drawable[drawable.length()];
        for(int i=0;i<drawable.length();i++)
            icons[i] = drawable.getDrawable(i);
        this.providersHashMap = providerHashMap;
        this.callback = callack;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder = null;
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (viewType){
            case 0:
                view = inflater.inflate(R.layout.no_policy_type_list_item,parent,false);
                viewHolder = new ViewHolderEmpty(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.policy_type_list_item,parent,false);
                viewHolder = new ViewHolderNonEmpty(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(holder instanceof ViewHolderEmpty){
            ViewHolderEmpty viewHolder = (ViewHolderEmpty) holder;
            viewHolder.title.setText(titles[position]);
            viewHolder.image.setImageDrawable(icons[position]);
            viewHolder.addPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.add(position);
                }
            });
        }
        else if(holder instanceof ViewHolderNonEmpty){
            Double totalCover = Policy.totalCover(typeWisePolicy.get(position));
            int cover = (int) Math.floor(totalCover);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            String moneyString = formatter.format(cover);

            ViewHolderNonEmpty viewHolder = (ViewHolderNonEmpty) holder;
            viewHolder.title.setText(titles[position]);
            viewHolder.image.setImageDrawable(icons[position]);
            viewHolder.amount.setText(moneyString.substring(0,moneyString.lastIndexOf(".00")));
            viewHolder.addPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.add(position);
                }
            });
            WhiteTextAdapter whiteTextAdapter = new WhiteTextAdapter(context,typeWisePolicy.get(position),providersHashMap);
            viewHolder.policies.setAdapter(whiteTextAdapter);
            viewHolder.policies.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        }
    }

    @Override
    public int getItemCount() {
        return typeWisePolicy.size();
    }

    @Override
    public int getItemViewType(int position) {
        return typeWisePolicy.get(position).size()==0 ? 0 : 1;
    }

    public class ViewHolderEmpty extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        ConstraintLayout addPolicy;
        public ViewHolderEmpty(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            addPolicy = itemView.findViewById(R.id.add);
        }
    }

    public class ViewHolderNonEmpty extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        LinearLayout addPolicy;
        TextView amount;
        RecyclerView policies;
        public ViewHolderNonEmpty(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            addPolicy = itemView.findViewById(R.id.add_policy);
            amount = itemView.findViewById(R.id.amount);
            policies = itemView.findViewById(R.id.policies);
        }
    }

    public interface ParentCallback {
        void add(int type);
    }
}
