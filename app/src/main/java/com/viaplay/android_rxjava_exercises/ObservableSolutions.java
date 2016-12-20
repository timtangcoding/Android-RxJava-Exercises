package com.viaplay.android_rxjava_exercises;

import com.viaplay.android_rxjava_exercises.types.BoxArt;
import com.viaplay.android_rxjava_exercises.types.JSON;
import com.viaplay.android_rxjava_exercises.types.Movie;
import com.viaplay.android_rxjava_exercises.types.Movies;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class ObservableSolutions extends ObservableExercises {

    /**
     * Return an Observable that emits a single value "Hello World"
     *
     * @return "Hello World!"
     */
    public Observable<String> exerciseHello() {
        return Observable.just("Hello World!");
    }

    /**
     * Transform the incoming Observable from "Hello" to "Hello [Name]" where [Name] is your name.
     *
     * @param "Hello Name!"
     */
    public Observable<String> exerciseMap(Observable<String> hello) {
        return hello.map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s + " Ben!";
            }
        });
    }

    /**
     * Given a stream of numbers, choose the even ones and return a stream like:
     * <p>
     * 2-Even
     * 4-Even
     * 6-Even
     */
    public Observable<String> exerciseFilterMap(Observable<Integer> nums) {
        return nums.filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer i) {
                return i % 2 == 0;
            }
        }).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return integer + "-Even";
            }
        });
    }


    /**
     * Flatten out all video in the stream of Movies into a stream of videoIDs
     *
     * @param movies
     * @return Observable of Integers of Movies.videos.id
     */
    public Observable<Integer> exerciseConcatMap(Observable<Movies> movies) {
        return movies.concatMap(new Func1<Movies, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(Movies movies) {
                return movies.videos.map(new Func1<Movie, Integer>() {
                    @Override
                    public Integer call(Movie movie) {
                        return movie.id;
                    }
                });
            }
        });
    }

    /**
     * Flatten out all video in the stream of Movies into a stream of videoIDs
     * <p>
     * Use flatMap this time instead of concatMap. In Observable streams
     * it is almost always flatMap that is wanted, not concatMap as flatMap
     * uses merge instead of concat and allows multiple concurrent streams
     * whereas concat only does one at a time.
     * <p>
     * We'll see more about this later when we add concurrency.
     *
     * @param movies
     * @return Observable of Integers of Movies.videos.id
     */
    public Observable<Integer> exerciseFlatMap(Observable<Movies> movies) {
        return movies.flatMap(new Func1<Movies, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Movies movies) {
                return movies.videos.map(new Func1<Movie, Integer>() {
                    @Override
                    public Integer call(Movie movie) {
                        return movie.id;
                    }
                });
            }
        });
    }

    /**
     * Retrieve the largest number.
     * <p>
     * Use reduce to select the maximum value in a list of numbers.
     */
    public Observable<Integer> exerciseReduce(Observable<Integer> nums) {
        return nums.reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer max, Integer item) {
                if (item > max) {
                    return item;
                } else {
                    return max;
                }
            }
        });
    }

    /**
     * Retrieve the id, title, and smallest box art url for every video.
     * <p>
     * Now let's try combining reduce() with our other functions to build more complex queries.
     * <p>
     * This is a variation of the problem we solved earlier, where we retrieved the url of the boxart with a
     * width of 150px. This time we'll use reduce() instead of filter() to retrieve the _smallest_ box art in
     * the boxarts list.
     * <p>
     * See Exercise 19 of ComposableListExercises
     */
    public Observable<JSON> exerciseMovie(Observable<Movies> movies) {
        return movies.flatMap(new Func1<Movies, Observable<JSON>>() {
            @Override
            public Observable<JSON> call(Movies movies) {
                return movies.videos.flatMap(new Func1<Movie, Observable<JSON>>() {
                    @Override
                    public Observable<JSON> call(final Movie movie) {
                        return movie.boxarts.reduce(new Func2<BoxArt, BoxArt, BoxArt>() {
                            @Override
                            public BoxArt call(BoxArt max, BoxArt box) {
                                int maxSize = max.height * max.width;
                                int boxSize = box.height * box.width;
                                if (boxSize < maxSize) {
                                    return box;
                                } else {
                                    return max;
                                }
                            }
                        }).map(new Func1<BoxArt, JSON>() {
                            @Override
                            public JSON call(BoxArt boxArt) {
                                return json("id", movie.id, "title", movie.title, "boxart", boxArt.url);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Combine 2 streams into pairs using zip.
     * <p>
     * a -> "one", "two", "red", "blue"
     * b -> "fish", "fish", "fish", "fish"
     * output -> "one fish", "two fish", "red fish", "blue fish"
     */
    public Observable<String> exerciseZip(Observable<String> a, Observable<String> b) {
        return Observable.zip(a, b, new Func2<String, String, String>() {
            @Override
            public String call(String x, String y) {
                return x + " " + y;
            }
        });
    }

    /**
     * Don't modify any values in the stream but do handle the error
     * and replace it with "default-value".
     */
    public Observable<String> handleError(Observable<String> data) {
        return data.onErrorResumeNext(Observable.just("default-value"));
    }

    /**
     * The data stream fails intermittently so return the stream
     * with retry capability.
     */
    public Observable<String> retry(Observable<String> data) {
        return data.retry();
    }

    // This function can be used to build JSON objects within an expression
    private static JSON json(Object... keyOrValue) {
        JSON json = new JSON();

        for (int counter = 0; counter < keyOrValue.length; counter += 2) {
            json.put((String) keyOrValue[counter], keyOrValue[counter + 1]);
        }

        return json;
    }

}
