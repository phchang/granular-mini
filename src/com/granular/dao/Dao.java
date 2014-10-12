package com.granular.dao;

public interface Dao<T> {

   long getNextId();

   Iterable<T> findAll();

   void save(T obj);
}
