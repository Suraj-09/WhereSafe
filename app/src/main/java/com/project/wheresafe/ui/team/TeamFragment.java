package com.project.wheresafe.ui.team;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.wheresafe.databinding.FragmentTeamBinding;

public class TeamFragment extends Fragment {

    private FragmentTeamBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TeamViewModel teamViewModel =
                new ViewModelProvider(this).get(TeamViewModel.class);

        binding = FragmentTeamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTeam;
        teamViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}