package com.alorma.expenses.ui.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
    @Bind(R.id.textDayBudgetTitle)
    TextView textDayBudgetTitle;
    @Bind(R.id.textRemainTitle)
    TextView textRemainTitle;
    @Bind(R.id.textDayBudget)
    TextView textDayBudget;
    @Bind(R.id.textRemain)
    TextView textRemain;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.header)
    View header;
    @Bind(R.id.fab)
    FloatingActionButton fab;

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
        textDayBudget.setText(String.format(TodayExpensesPresenter.FORMAT, max));
        textRemain.setText(String.format(TodayExpensesPresenter.FORMAT, max - current));
        changeColorsGraph(current, max);

        changeColorsHeader(current, max);

    }

    private void changeColorsGraph(float current, float max) {
        //Create data series track
        SeriesItem seriesItem = new SeriesItem.Builder(ContextCompat.getColor(getActivity(), R.color.accent))
                .setRange(0, max, 0)
                .setLineWidth(48f)
                .setInitialVisibility(false)
                .setInterpolator(new DecelerateInterpolator())
                .build();

        int seriesIndex = decoView.addSeries(seriesItem);

        DecoEvent.Builder decoEvent = new DecoEvent.Builder(current)
                .setIndex(seriesIndex)
                .setDuration(700);

        if (current >= max) {
            decoEvent.setColor(ContextCompat.getColor(getActivity(), R.color.md_red_300));
            decoEvent.setDuration(1500);
        } else if ((current / max) > 0.7f) {
            decoEvent.setColor(ContextCompat.getColor(getActivity(), R.color.md_yellow_800));
            decoEvent.setDuration(1000);
        }

        decoView.addEvent(decoEvent.build());
    }

    private void changeColorsHeader(float current, float max) {

        int colorFromBackground = R.color.md_teal_600;
        int colorFromTexts = R.color.md_teal_A400;

        int colorToBackground = colorFromBackground;
        int colorToTexts = colorFromTexts;

        if (current >= max) {
            colorToBackground = R.color.md_red_600;
            colorToTexts = R.color.md_grey_300;
        } else if ((current / max) > 0.7f) {
            colorToBackground = R.color.md_yellow_600;
            colorToTexts = R.color.md_yellow_900;
        }
        colorFromBackground = ContextCompat.getColor(getContext(), colorFromBackground);
        colorToBackground = ContextCompat.getColor(getContext(), colorToBackground);
        ValueAnimator colorAnimationBackground = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromBackground, colorToBackground);
        colorAnimationBackground.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                header.setBackgroundColor(color);

                int[][] states = new int[][] {{android.R.attr.state_checked}, {}};
                int[] colors = new int[]{color, color};
                ColorStateList stateList = new ColorStateList(states, colors);

                fab.setBackgroundTintList(stateList);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().getWindow().setStatusBarColor(color);
                }
            }
        });
        colorAnimationBackground.setDuration(1000);
        colorAnimationBackground.start();

        colorFromTexts = ContextCompat.getColor(getContext(), colorFromTexts);
        colorToTexts = ContextCompat.getColor(getContext(), colorToTexts);
        ValueAnimator colorAnimationTexts = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromTexts, colorToTexts);
        colorAnimationTexts.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                textDayBudgetTitle.setTextColor(color);
                textRemainTitle.setTextColor(color);
            }
        });
        colorAnimationTexts.setDuration(1000);
        colorAnimationTexts.start();
    }

    private void setAdapter(List<Expense> expenses) {
        adapter.addAll(expenses);
    }
}
