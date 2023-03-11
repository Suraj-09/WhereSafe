package com.project.wheresafe.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.project.wheresafe.R;
import com.project.wheresafe.databinding.FragmentHomeBinding;
import com.project.wheresafe.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createChart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createChart(){
        FragmentActivity mActivity = getActivity();
        LineChart lineChart = mActivity.findViewById(R.id.line_chart);
        BarChart barChart = mActivity.findViewById(R.id.bar_chart);

        initializeLineChart(lineChart);
        initializeBarChart(barChart);


        // entries for line chart
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 5));

        // entries for bar chart
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0,10));
        values.add(new BarEntry(1,20));
        values.add(new BarEntry(2,5));

        // Create labels for the data sets
        String lineLabel = "Line Chart";
        LineDataSet lineDataSet = new LineDataSet(entries,lineLabel);

        String barLabel = "Bar Chart";
        BarDataSet barDataSet = new BarDataSet(values,barLabel);

        customizeLineDataSet(lineDataSet);


        LineData dataLine = new LineData(lineDataSet);
        lineChart.setData(dataLine);
        lineChart.invalidate();

        BarData dataBar = new BarData(barDataSet);
        barChart.setData(dataBar);
        barChart.invalidate();
    }

    private void initializeLineChart(LineChart lineChart) {
        // Customize chart appearance and behavior here
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setPinchZoom(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisRight().setDrawGridLines(true);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(0.5f); // sets value for x-axis to be incremented
        //chart.setMarker(marker);      // could be implemented later...
                                        // displays a customized pop-up whenever a value in the chart is clicked on
                                        // https://github.com/PhilJay/MPAndroidChart/wiki/MarkerView
        lineChart.invalidate();     // invalidates current layout  and triggers new layout pass
                                    // causes chart to be redrawn with new changes made
                                    // call this whenever you update the chart
    }

    private void initializeBarChart(BarChart barChart) {
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(true);
        barChart.setPinchZoom(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisRight().setDrawGridLines(true);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        barChart.invalidate();
    }
    private void customizeLineDataSet(LineDataSet lineDataSet) {
        // Customize dataset appearance and behavior here
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(true);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
    }
}