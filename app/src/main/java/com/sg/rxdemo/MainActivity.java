package com.sg.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="MyApp";
    private String greetings="Hello From RxJava";
    private Observable<String> myObservable;
    private Observer<String> myObserver;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myObservable=Observable.just(greetings);
        textView=findViewById(R.id.tv);
        myObservable.subscribeOn(Schedulers.io());
        myObservable.observeOn(AndroidSchedulers.mainThread());
        myObserver=new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG,"onSubscribe invoked");

            }

            @Override
            public void onNext(String s) {
                Log.i(TAG,"onNext invoked");
                textView.setText(s);


            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"onError invoked");
            }

            @Override
            public void onComplete() {
                Log.i(TAG,"onComplete invoked");


            }
        };
        myObservable.subscribe(myObserver);
    }
}
