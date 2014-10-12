package com.granular.dao;

import java.sql.SQLException;

public interface Dao<T> {

   long getNextId();

   T findById(Long id) throws SQLException;

   Iterable<T> findAll() throws SQLException;

   void save(T obj) throws SQLException;

   void update(T obj) throws SQLException;

   void delete(T obj) throws SQLException;

}
