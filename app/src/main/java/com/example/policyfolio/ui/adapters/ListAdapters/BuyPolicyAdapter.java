package com.example.policyfolio.ui.adapters.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.R;
import com.example.policyfolio.data.local.classes.InsuranceProducts;
import com.example.policyfolio.data.local.classes.InsuranceProvider;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BuyPolicyAdapter extends RecyclerView.Adapter<BuyPolicyAdapter.ViewHolder> {
    private Context context;
    private ArrayList<InsuranceProducts> products;
    private String[] frequency;
    private ParentCallback callback;

    public BuyPolicyAdapter(Context context, ArrayList<InsuranceProducts> products, ParentCallback callback){
        this.context = context;
        this.products = products;
        frequency = context.getResources().getStringArray(R.array.premium_frequency);
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.buy_policy_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

        holder.name.setText(products.get(position).getName());
        String moneyString = formatter.format(Double.parseDouble(products.get(position).getPremium()));
        holder.premium.setText(moneyString.substring(0,moneyString.lastIndexOf(".00")) + " - " + frequency[products.get(position).getFrequency()]);
        moneyString = formatter.format(Double.parseDouble(products.get(position).getSumAssured()));
        holder.cover.setText(moneyString.substring(0,moneyString.lastIndexOf(".00")));

        callback.setProvider(holder.image,holder.provider,products.get(position).getProviderId());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView provider;
        TextView premium;
        TextView cover;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            provider = itemView.findViewById(R.id.insurance_provider);
            premium = itemView.findViewById(R.id.premium);
            cover = itemView.findViewById(R.id.cover);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface ParentCallback {
        void setProvider(ImageView image,TextView textView,Long providerId);
    }
}
