package com.alorma.expenses.data;

import android.content.Context;

import com.alorma.expenses.bean.Expense;
import com.alorma.expenses.bean.ExpensesList;
import com.alorma.expenses.data.callbacks.TodayExpensesCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bernat.borras on 31/12/15.
 */
public class TodayExpensesPresenter extends Presenter<TodayExpensesCallback> {

    public static final String FORMAT = "%.2f €";
    private Context context;

    public TodayExpensesPresenter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void start() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    try {
                        InputStream is = context.getAssets().open("data.json");
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        String json = new String(buffer, "UTF-8");
                        subscriber.onNext(json);
                        subscriber.onCompleted();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        subscriber.onError(ex);
                    }
                }
            }
        })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s != null;
                    }
                }).map(new Func1<String, List<Expense>>() {
            @Override
            public List<Expense> call(String s) {
                return new Gson().fromJson(s, ExpensesList.class);
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<Expense>>() {
                    @Override
                    public void call(List<Expense> expenses) {
                        float total = 65;
                        float current = 0;
                        for (Expense expense : expenses) {
                            current += expense.value;
                        }
                        getCallback().onExpensesCalculated(current, total);
                    }
                })
                .subscribe(new Subscriber<List<Expense>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Expense> expenses) {
                        getCallback().onExpensesLoaded(expenses);
                    }
                });
    }

    @Override
    public TodayExpensesCallback getNullCallback() {
        return new TodayExpensesCallback.Null();
    }
}
