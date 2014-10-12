package com.granular.dao;

import com.granular.model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InventoryDao extends AbstractBasicDao<Product> implements Dao<Product>  {

   public InventoryDao() {
   }

   public InventoryDao(Connection conn) {
      super(conn);
   }

   @Override
   public Product findById(Long id) throws SQLException {
      return null;
   }

   @Override
   public Iterable<Product> findAll() throws SQLException {

      Statement st = null;
      ResultSet rs = null;

      st = conn.createStatement();

      rs = st.executeQuery("select name, code, balance from inventory");    // run the query

      while (rs.next()) {
         String name = rs.getString("name");
         String code = rs.getString("code");
         double balance = rs.getDouble("balance");

         System.out.println(name + ", " + code + ", " + balance);
      }

      st.close();

      return super.findAll();
   }

   @Override
   public void update(Product obj) throws SQLException {

   }

   @Override
   public void delete(Product obj) throws SQLException {

   }
}
