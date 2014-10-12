package com.granular.dao;

import com.granular.model.Plan;
import com.granular.model.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PlanDao extends AbstractBasicDao<Plan> implements Dao<Plan> {

   public PlanDao(Connection conn) {
      super(conn);
   }

   @Override
   public Plan findById(Long id) throws SQLException {
      return null;
   }

   @Override
   public void save(Plan obj) throws SQLException {

//      executeQuery("", new ResultSetHandler() {
//         @Override
//         public List find(ResultSet rs) {
//            return null;
//         }
//
//         @Override
//         public Object findOne() {
//            return null;
//         }
//      });

   }

   @Override
   public void update(Plan obj) throws SQLException {

   }

   @Override
   public void delete(Plan obj) throws SQLException {

   }
}