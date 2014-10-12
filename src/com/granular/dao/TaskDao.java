package com.granular.dao;

import com.granular.model.Task;

import java.sql.SQLException;

public class TaskDao extends AbstractBasicDao<Task> implements Dao<Task> {

   @Override
   public Task findById(Long id) throws SQLException {
      return null;
   }

   @Override
   public void update(Task obj) throws SQLException {

   }

   @Override
   public void delete(Task obj) throws SQLException {

   }
}
