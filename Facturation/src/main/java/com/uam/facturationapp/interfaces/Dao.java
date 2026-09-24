package com.uam.facturationapp.interfaces;

import javafx.collections.ObservableList;

import java.util.Optional;

public interface Dao<T, ID> {

    ObservableList<T> findAll();

    Optional<T> findById(ID id);

    boolean save(T entity);

    boolean update(ID id, T entity);

    boolean delete(ID id);
}