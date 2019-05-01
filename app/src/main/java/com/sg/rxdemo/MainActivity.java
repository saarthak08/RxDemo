package com.sg.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="MyApp";
    private String greetings="Hello From RxJava";
    private String[] greetingsarray={"Hello Lallu","Hello A", "Hello B", "Hello C","Hello A","Hello B","I am mad"};
    private Observable<String> myObservable;
    private DisposableObserver<String> myObserver;
    private DisposableObserver<String> myObserver2;
    private DisposableObserver<String[]> arrayObserver;
    private Observable<String[]> myStringObservable;
    private Observable<String> singleObservable;
    private Observable<Integer> integerObservable;
    private final static String TAGS = "myApp";
    private Observable<Student> myObservableStudent;
    private DisposableObserver<Student> myObserverStudent;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();

    private TextView textView;
   // private Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.tv);
        //myObservable=Observable.just(greetings);
        /*

        Just operator converts data stream into observable
        .If an array is taken then just operator will convert whole array into observalbe unlike fromArray
        operator.
        If values are passed to just separated using commas then it converts each value to observable
        one by one.  */

        myStringObservable=Observable.just(greetingsarray); //all at once
       singleObservable=Observable.just("Hello 1", "Hello 2"," Hello 3"); //one by one array of observers. Same as fromArray in case of array of data stream as input.
        myObservable=Observable.fromArray(greetingsarray);
        integerObservable=Observable.range(1,20); //emit values ranging from 1 to 20

/////////////////////////////////////////////////////////////////////////////////////////
        myObservableStudent=Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {


                ArrayList<Student> studentArrayList=getStudents();

                for(Student student:studentArrayList){


                    emitter.onNext(student);

                }

                emitter.onComplete();



            }
        });
/////////////////////////////////////////////////////////////////////////////////////////


        compositeDisposable.add(
                myObservableStudent
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Student, Student>() {
                            @Override
                            public Student apply(Student student) throws Exception {
                                student.setEmail(student.getEmail().toUpperCase());
                                return student;
                            }
                        })
                        .flatMap(new Function<Student, ObservableSource<Student>>() {
                            @Override
                            public ObservableSource<Student> apply(Student student) throws Exception {
                                Student student1=new Student();
                                student1.setEmail(student.getEmail());
                                Student student2=new Student();
                                student2.setEmail("New Member: "+student.getEmail());
                                return Observable.just(student,student1,student2);
                            }
                        })
                        .concatMap(new Function<Student, ObservableSource<Student>>() {   //Preserves the order of the data emitted but waits for the observable to finish its task
                            @Override
                            public ObservableSource<Student> apply(Student student) throws Exception {
                                Student student1=new Student();
                                student1.setEmail(student.getEmail());
                                Student student2=new Student();
                                student2.setEmail("New Member: "+student.getEmail());
                                return Observable.just(student,student1,student2);
                            }
                        })
                        .subscribeWith(getObserver())
        );
/////////////////////////////////////////////////////////////////////////////////////////


        compositeDisposable.add(integerObservable.buffer(4).filter(new Predicate<List<Integer>>() {
            @Override
            public boolean test(List<Integer> integers) throws Exception {
                boolean r=false;
                for(Integer i:integers)
                {
                    if(i>15)
                    {
                        r=true;
                    }
                    else
                        r=false;
                }
                return r;
            }
        }).subscribeWith(new DisposableObserver<List<Integer>>() {
            @Override
            public void onNext(List<Integer> integers) {
                Log.i(TAG,"onNext invoked");
                for(Integer i:integers)
                {
                    Log.i(TAG,""+i);
                }


            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"onError invoked");


            }

            @Override
            public void onComplete() {
                Log.i(TAG,"onComplete invoked");


            }
        }));

/////////////////////////////////////////////////////////////////////////////////////////



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
/////////////////////////////////////////////////////////////////////////////////////////

       myObserver=new DisposableObserver<String>() {
           @Override
           public void onNext(String s) {
               Log.i(TAG,"onNext invoked");
               Log.i(TAG,"Yo "+s);
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
                .distinct().skip(1).skipLast(1).subscribeWith(myObserver));
////////////////////////////////////////////////////////////////////////////////////////////////////
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
/////////////////////////////////////////////////////////////////////////////////////////

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


    private DisposableObserver getObserver() {

        myObserverStudent = new DisposableObserver<Student>() {
            @Override
            public void onNext(Student s) {


                Log.i(TAGS, " onNext invoked with " + s.getEmail());
            }

            @Override
            public void onError(Throwable e)

            {
                Log.i(TAGS, " onError invoked");
            }

            @Override
            public void onComplete() {

                Log.i(TAGS, " onComplete invoked");
            }
        };

        return myObserverStudent;
    }


    private ArrayList<Student> getStudents() {

        ArrayList<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setName(" student 1");
        student1.setEmail(" student1@gmail.com ");
        student1.setAge(27);
        students.add(student1);

        Student student2 = new Student();
        student2.setName(" student 2");
        student2.setEmail(" student2@gmail.com ");
        student2.setAge(20);
        students.add(student2);

        Student student3 = new Student();
        student3.setName(" student 3");
        student3.setEmail(" student3@gmail.com ");
        student3.setAge(20);
        students.add(student3);

        Student student4 = new Student();
        student4.setName(" student 4");
        student4.setEmail(" student4@gmail.com ");
        student4.setAge(20);
        students.add(student4);

        Student student5 = new Student();
        student5.setName(" student 5");
        student5.setEmail(" student5@gmail.com ");
        student5.setAge(20);
        students.add(student5);

        return students;


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
