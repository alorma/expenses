package com.alorma.expenses.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.alorma.expenses.R;
import com.alorma.expenses.bean.Expense;
import com.alorma.expenses.data.TodayExpensesPresenter;
import com.alorma.expenses.data.callbacks.TodayExpensesCallback;
import com.alorma.expenses.ui.adapter.TodayExpensesAdapter;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bernat.borras on 31/12/15.
 */
public class TodayFragment extends Fragment implements TodayExpensesCallback {

    @Bind(R.id.dynamicArcView)
    DecoView decoView;
    @Bind(R.id.textPercentage)
    TextView textPercentage;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    private TodayExpensesPresenter todayExpensesPresenter;
    private TodayExpensesAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_today, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, getActivity());

        setUpGraph();
        setUpList();
    }

    private void setUpPresenter() {
        todayExpensesPresenter = new TodayExpensesPresenter(getActivity());

        todayExpensesPresenter.start(this);
    }

    private void setUpGraph() {
        // Create background track
        decoView.addSeries(new SeriesItem.Builder(Color.argb(128, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(48f)
                .build());
    }

    private void setUpList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new TodayExpensesAdapter();

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        setUpPresenter();
    }

    @Override
    public void onStop() {
        todayExpensesPresenter.stop();
        super.onStop();
    }

    @Override
    public void onExpensesCalculated(float current, float max) {
        setGraphData(current, max);
    }

    @Override
    public void onExpensesLoaded(final List<Expense> expenses) {
        ViewCompat.setAlpha(recyclerView, 0f);
        adapter.clear();
        setAdapter(expenses);
        ViewCompat.animate(recyclerView).alpha(1f).setDuration(600);
    }

    private void setGraphData(float current, float max) {
        //Create data series track
        SeriesItem seriesItem = new SeriesItem.Builder(ContextCompat.getColor(getActivity(), R.color.accent))
                .setRange(0, max, 0)
                .setLineWidth(48f)
                .setInitialVisibility(false)
                .setInterpolator(new AccelerateInterpolator())
                .build();

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textPercentage.setText(String.format(TodayExpensesPresenter.FORMAT, currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        int i = decoView.addSeries(seriesItem);

        decoView.addEvent(new DecoEvent.Builder(current)
                .setIndex(i)
                .setDuration(500)
                .build());
    }

    private void setAdapter(List<Expense> expenses) {
        adapter.addAll(expenses);
    }
}
