package com.delta.test.rxjavaoperater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    int a = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //just
        //最多十个参数
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "accept: " + integer);
                    }
                });

        //fromArray
        // repeat
        //repeatWhen
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        Observable.fromArray(integers).repeat(3)/*.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Observable<Object> objectObservable) throws Exception {
                return objectObservable.delay(5, TimeUnit.SECONDS);
            }
        })*/.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "accept: " + integer);
            }
        });

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    if (!emitter.isDisposed()) {
                        for (int i = 1; i < 5; i++) {
                            emitter.onNext(i);
                        }
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "accept: " + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.i(TAG, "accept: ");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.i(TAG, "run: ");
            }


        }, new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                Log.i(TAG, "accept: ");
            }
        });

        //未使用defer,Observable对象在调用just时生成
        Observable<Integer> observable1 = Observable.just(a);

        a = 2;
        observable1.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "accept( no defer): " + integer);
            }
        });

        //使用defer
        a = 3;
        Observable<Integer> observable2 = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(a);
            }
        });

        a = 4;
        observable2.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "accept( have defer): " + integer);
            }
        });

        //使用range
        Observable.range(1, 4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "accept: " + integer);
                    }
                });

        Observable.interval(5, TimeUnit.SECONDS, Schedulers.trampoline()).just(12)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "accept: " + integer);
                    }
                });

    }
}
