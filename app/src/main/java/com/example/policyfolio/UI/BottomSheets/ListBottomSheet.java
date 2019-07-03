package com.example.policyfolio.UI.BottomSheets;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBottomSheet extends Fragment {

    private View rootLayout;

    private TextView title;
    private RecyclerView list;
    private RecyclerView.Adapter adapter;
    private int type;


    public ListBottomSheet() {
        // Required empty public constructor
    }

    public ListBottomSheet(int type, RecyclerView.Adapter adapter){
        this.type = type;
        this.adapter = adapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootLayout = inflater.inflate(R.layout.bottom_sheet_list, container, false);

        title = rootLayout.findViewById(R.id.title);
        list = rootLayout.findViewById(R.id.list);

        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        setTitle();
        return rootLayout;
    }

    private void setTitle() {
        switch (type){
            case Constants.ListTypes.GENDER:
                title.setText("Gender");
                break;
            case Constants.ListTypes.PREMIUM_FREQUENCY:
                title.setText("Premium Frequency");
                break;
            case Constants.ListTypes.NOMINEE:
                title.setText("Nominee");
                break;
            case Constants.ListTypes.RELATIONSHIPS:
                title.setText("Relationships");
                break;
        }
    }

}
