package com.granular.dao;

public interface Dao<T> {

   Iterable<T> findAll();

   void save(T obj);

}
