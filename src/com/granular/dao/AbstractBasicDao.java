package com.granular.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBasicDao<T> implements Dao<T> {

   private List<T> objects;
   protected Connection conn;

   public AbstractBasicDao() {
      objects = new ArrayList<>();
   }

   public AbstractBasicDao(Connection conn) {
      this.conn = conn;
      objects = new ArrayList<>();
   }

   // todo switch to PreparedStatement to prevent SQL injection

   protected void executeUpdate(String update) throws SQLException {
      PreparedStatement st = conn.prepareStatement(update);
      st.executeUpdate(update);

      st.close();
   }

   protected void executeQuery(String query, ResultSetHandler handler) throws SQLException {
      Statement st;
      ResultSet rs;

      st = conn.createStatement();
      rs = st.executeQuery(query);

      handler.handle(rs);

      rs.close();
      st.close();
   }

   @Override
   public long getNextId() {
      return objects.size() + 1;
   }

   @Override
   public Iterable<T> findAll() throws SQLException {
      return new ArrayList<>(objects);
   }

   @Override
   public void save(T obj) throws SQLException {
      objects.add(obj);
   }
}
