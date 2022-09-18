package dev.vulcanth.nyel.gerementions.other.model;

import java.util.stream.Stream;

public interface Model<String, T> {

    void create(T model);

    void remove(String s);

    T get(String s);

    Stream<T> search(String s);

}

