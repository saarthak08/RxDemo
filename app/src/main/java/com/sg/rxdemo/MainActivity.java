package com.sg.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="MyApp";
    private String greetings="Hello From RxJava";
    private String[] greetingsarray={"Hello A, Hello B, Hello C"};
    private Observable<String> myObservable;
    private DisposableObserver<String> myObserver;
    private DisposableObserver<String> myObserver2;
    private DisposableObserver<String[]> arrayObserver;
    private Observable<String[]> myStringObservable;
    private Observable<String> singleObservable;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    private TextView textView;
   // private Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.tv);
        myObservable=Observable.just(greetings);  /*

        Just operator converts data stream into observable
        .If an array is taken then just operator will convert whole array into observalbe unlike fromArray
        operator.
        If values are passed to just separated using commas then it converts each value to observable
        one by one.  */

        myStringObservable=Observable.just(greetingsarray); //all at once
       singleObservable=Observable.just("Hello 1", "Hello 2"," Hello 3"); //one by one array of observers







       // myObservable.subscribeOn(Schedulers.io());
        //myObservable.observeOn(AndroidSchedulers.mainThread());
       /* myObserver=new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG,"onSubscribe invoked");
                disposable=d;

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
        };*/

       myObserver=new DisposableObserver<String>() {
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
       //compositeDisposable.add(myObserver);
        //myObservable.subscribe(myObserver);
        compositeDisposable.add(myObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(myObserver));

        myObserver2=new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.i(TAG,"onNext invoked");
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
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
        compositeDisposable.add(singleObservable.subscribeWith(myObserver2));
      //  compositeDisposable.add(myObserver2);
       // myObservable.subscribe(myObserver2);

        arrayObserver=new DisposableObserver<String[]>() {
            @Override
            public void onNext(String[] strings) {
                Log.i(TAG,"onNext invoked");

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
        compositeDisposable.add(myStringObservable.subscribeWith(arrayObserver));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
       // myObserver.dispose();
       // myObserver2.dispose();
      //  disposable.dispose();
    }
}
