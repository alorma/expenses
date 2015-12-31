package com.alorma.expenses.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.expenses.bean.Expense;
import com.alorma.expenses.data.TodayExpensesPresenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bernat.borras on 31/12/15.
 */
public class TodayExpensesAdapter extends RecyclerView.Adapter<TodayExpensesAdapter.Holder> {

    private List<Expense> expenses = new ArrayList<>();

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2
                , parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Expense expense = expenses.get(position);
        holder.text1.setText(expense.name);
        holder.text2.setText(String.format(TodayExpensesPresenter.FORMAT, expense.value));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void add(Expense expense) {
        expenses.add(expense);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Expense> expensesList) {
        expenses.addAll(expensesList);
        notifyDataSetChanged();
    }

    public void clear() {
        expenses.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;

        public Holder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            text2 = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }
}
