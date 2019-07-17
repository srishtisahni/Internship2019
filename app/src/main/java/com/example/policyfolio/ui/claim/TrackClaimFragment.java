package com.example.policyfolio.ui.claim;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.R;
import com.example.policyfolio.ui.adapters.PagerAdapters.ClaimPagerAdapter;
import com.example.policyfolio.viewmodels.ClaimViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackClaimFragment extends Fragment implements TrackClaimCallback {

    private View rootView;
    private ClaimViewModel viewModel;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ClaimPagerAdapter adapter;

    private OnGoingFragment onGoingFragment;
    private ResolvedFragment resolvedFragment;

    public TrackClaimFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_track_claim, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(ClaimViewModel.class);
        viewModel.initiateRepo(getContext());

        tabLayout = rootView.findViewById(R.id.tabLayout);
        viewPager = rootView.findViewById(R.id.viewPager);

        onGoingFragment = new OnGoingFragment(viewModel,this);
        resolvedFragment = new ResolvedFragment(viewModel,this);

        setUpPagerAdapter();

        return rootView;
    }

    private void setUpPagerAdapter() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(onGoingFragment);
        fragments.add(resolvedFragment);

        adapter = new ClaimPagerAdapter(getActivity().getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Ongoing");
        tabLayout.getTabAt(1).setText("Resolved");
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

}
