package com.delta.test.rxjavaoperater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
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
                        Log.i(TAG, "just-accept: " + integer);
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
                Log.i(TAG, "fromArray-accept: " + integer);
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

        //interval无效果
        /*Observable.interval(5, TimeUnit.SECONDS, Schedulers.trampoline()).just(12)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "accept: " + integer);
                    }
                });*/

        //timer无效果
        Log.i(TAG, "onCreate: 测试延时");
        Observable.timer(5, TimeUnit.SECONDS, Schedulers.trampoline()).just(12).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer aLong) throws Exception {
                Log.i(TAG, "accept: " + aLong);
            }
        });

        //map
        Observable.just(1, 2, 3).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return integer + " hehe";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, "map-accept: " + s);
            }
        });

        //flatMap，concatMap
        List<String> course_tom = new ArrayList<>();
        course_tom.add("语文");
        course_tom.add("数学");
        course_tom.add("英语");
        Student student_tom = new Student("tom", course_tom);
        List<String> course_cat = new ArrayList<>();
        course_cat.add("物理");
        course_cat.add("化学");
        course_cat.add("生物");
        Student student_cat = new Student("cat", course_cat);
        List<String> course_dog = new ArrayList<>();
        course_dog.add("政治");
        course_dog.add("历史");
        course_dog.add("地理");
        Student student_dog = new Student("dog", course_dog);
        Observable.just(student_tom, student_cat, student_dog).flatMap(new Function<Student, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Student student) throws Exception {
                return Observable.fromIterable(student.getCourse());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "flatMap-accept: " + s);
                    }
                });

        //switchMap
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8).switchMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                //每次生成新的Observable都开启一个新的线程，在不同线程
                return Observable.just(integer + "").subscribeOn(Schedulers.io());
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "flatMap-accept: " + s);
                    }
                });

        //split rxjava2弃用
        /*String [] arr = new String[]{"Wh","at i","s y","ou","r ","na","me"};
        Observable.fromArray(arr)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        return Observable.fromArray(s.split(" "));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "split-accept: "+s);
                    }
                });*/

        System.out.println("-----------------------------filter---------------------------");
        //Observable.just()
        //filter
        //take
        Observable.interval(1, TimeUnit.SECONDS).take(1).filter(new Predicate<Long>() {
            @Override
            public boolean test(@NonNull Long aLong) throws Exception {
                return aLong > 5;
            }
        }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                Log.i(TAG, "accept: " + aLong);
            }
        });

        //takeLast
        Observable.just(1, 2, 3, 4, 5, 6).takeLast(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "takeLast-accept: " + integer);
            }
        });

        //last
        Observable.just(1, 2, 3, 4, 5).last(0).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.i(TAG, "last-accept: " + s);
            }
        });

        //skip
        Observable.just(1, 2, 3, 4, 5).skip(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.i(TAG, "skip-accept: " + s);
            }
        });


        //first
        Observable.just(1, 2, 3, 4, 5).first(0).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.i(TAG, "first-accept: " + s);
            }
        });

        //elementAt
        Observable.just(1, 2, 3, 4, 5).elementAt(4, 0).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.i(TAG, "elementAt-accept: " + s);
            }
        });

        //sample
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
//                for (int i = 0; ; i++) {
//                    e.onNext(9);
//                }
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                //.sample(2, TimeUnit.SECONDS)
//                .subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(@NonNull Integer s) throws Exception {
//                Log.i(TAG, "sample-accept: " + s);
//            }
//        });

        //throttleFirst
        /*Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(8);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(2, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.i(TAG, "throttleFirst-accept: " + s);
            }
        });*/

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(8);
                    Thread.sleep(3100);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleWithTimeout(3, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
                Log.i(TAG, "throttleWithTimeout-accept: " + s);
            }
        });




        //timeout
        /*Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    e.onNext(i);
                }
            }
        }).timeout(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "timeout-accept: " + integer);
                    }
                });*/

        //distinct
        Observable.just(1, 2, 3, 5, 7, 3, 1, 5, 8, 7)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "distinct-accept: " + integer);
                    }
                });

        //distinctUntilChanged
        Observable.just(1, 2, 3, 3, 5, 7, 7, 3, 1, 5)
                .distinctUntilChanged()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "distinctUntilChanged-accept: " + integer);
                    }
                });

        //ofType
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext("he");
                e.onNext("du");
                e.onNext("sue");
            }
        }).ofType(String.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object object) throws Exception {
                        Log.i(TAG, "ofType-accept: " + object.toString());
                    }
                });

        //merge
        //startWith
        Observable<Integer> odds = Observable.just(1, 3, 5, 7, 9, 11, 13, 15, 17, 19).subscribeOn(Schedulers.io());
        Observable<Integer> evens = Observable.just(2, 4, 6, 8, 10, 12, 14, 16, 18, 20).subscribeOn(Schedulers.io());
        Observable.merge(odds, evens).startWith(0).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "merge-accept: " + integer);
            }
        });

        //zip
        Observable<Integer> odds2 = Observable.just(1, 3, 5, 7, 9);
        Observable<Integer> evens2 = Observable.just(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        Observable.zip(odds2, evens2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                return integer + "----" + integer2;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "zip-accept: " + s);
                    }
                });

        Observable<Integer> odds3 = Observable.just(1, 3, 5, 7, 9);
        Observable<Integer> evens3 = Observable.just(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        Observable.combineLatest(odds3, evens3, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                return integer + "----" + integer2;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "combineLatest-accept: " + s);
                    }
                });

        //join
        //groupJoin
        final String[] str_w = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        Observable<String> odds4 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    e.onNext(str_w[i] + " odd");
                    Thread.sleep(1000);
                }
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> evens4 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    e.onNext(i + " even");
                    Thread.sleep(1000);
                }

            }
        }).subscribeOn(Schedulers.io());

        evens4.join/*.groupJoin*/(odds4, new Function<String, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(@NonNull String integer) throws Exception {
                        return Observable.timer(2, TimeUnit.SECONDS);
                    }
                }, new Function<String, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(@NonNull String integer) throws Exception {
                        return Observable.timer(0, TimeUnit.SECONDS);
                    }
                }, new BiFunction<String, String, String>() {
                    @Override
                    public String apply(@NonNull String integer, @NonNull String integer2) throws Exception {
                        return integer + "----" + integer2;
                    }
                }
                /*, new BiFunction<String, Observable<String>, String>() {

                    @Override
                    public String apply(@NonNull String s, @NonNull Observable<String> stringObservable) throws Exception {
                        stringObservable.subscribe(new Consumer<String>() {
                            @Override
                            public void accept(@NonNull String s) throws Exception {
                                Log.i(TAG, "groupJoin1-accept: "+s);
                            }
                        });
                        return s;
                    }
                }*/
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, "groupJoin2-accept: " + s);
            }
        });






    }
}
