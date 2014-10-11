package com.granular.dao;

import java.util.ArrayList;
import java.util.List;

public class AbstractBasicDao<T> implements Dao<T> {

   private List<T> objects;

   public AbstractBasicDao() {
      objects = new ArrayList<>();
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
