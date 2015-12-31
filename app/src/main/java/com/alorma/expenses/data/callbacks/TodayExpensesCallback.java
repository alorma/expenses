package com.alorma.expenses.data.callbacks;

import com.alorma.expenses.bean.Expense;

import java.util.List;

/**
 * Created by bernat.borras on 31/12/15.
 */
public interface TodayExpensesCallback {

    void onExpensesLoaded(List<Expense> expenses);
    void onExpensesCalculated(float current, float max);

    class Null implements TodayExpensesCallback{

        @Override
        public void onExpensesLoaded(List<Expense> expenses) {

        }

        @Override
        public void onExpensesCalculated(float current, float max) {

        }
    }
}
