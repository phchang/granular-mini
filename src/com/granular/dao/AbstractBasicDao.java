package com.granular.dao;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBasicDao<T> implements Dao<T> {

   protected List<T> objects;

   public AbstractBasicDao() {
      objects = new ArrayList<>();
   }

   @Override
   public long getNextId() {
      return objects.size() + 1;
   }

   @Override
   public Iterable<T> findAll() {
      return new ArrayList<>(objects);
   }

   @Override
   public void save(T obj) {
      objects.add(obj);
   }
}