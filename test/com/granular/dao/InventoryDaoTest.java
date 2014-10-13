package com.granular.dao;

import com.granular.dao.impl.InventoryDao;
import com.granular.model.Product;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryDaoTest {

   @Test
   public void testInventoryAvailable() {
      Product product1 = new Product("Product 1", "AAA", 100d, "unit1");
      Product product2 = new Product("Product 2", "BBB", 200d, "unit2");
      Product product3 = new Product("Product 3", "CCC", 300d, "unit3");
      Product product4 = new Product("Product 4", "DDD", 400d, "unit4");

      InventoryDao dao = new InventoryDao();
      dao.save(product1);
      dao.save(product2);
      dao.save(product3);
      dao.save(product4);

      assertFalse(dao.isAmountAvailable("AAA", 200d));
      assertTrue(dao.isAmountAvailable("AAA", 99d));
      assertTrue(dao.isAmountAvailable("AAA", 100d));
   }

   @Test
   public void testDebit() {
      Product product1 = new Product("Product 1", "AAA", 100d, "unit1");
      Product product2 = new Product("Product 2", "BBB", 200d, "unit2");
      Product product3 = new Product("Product 3", "CCC", 300d, "unit3");
      Product product4 = new Product("Product 4", "DDD", 400d, "unit4");

      InventoryDao dao = new InventoryDao();
      dao.save(product1);
      dao.save(product2);
      dao.save(product3);
      dao.save(product4);

      dao.debitBalance("BBB", 50d);

      assertEquals(Double.valueOf(150), product2.getBalance());

      // make sure nothing else was affected
      assertEquals(Double.valueOf(100), product1.getBalance());
      assertEquals(Double.valueOf(300), product3.getBalance());
      assertEquals(Double.valueOf(400), product4.getBalance());
   }
}
