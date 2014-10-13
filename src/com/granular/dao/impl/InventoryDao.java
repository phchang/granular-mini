package com.granular.dao.impl;

import com.granular.dao.AbstractBasicDao;
import com.granular.dao.Dao;
import com.granular.model.Product;

public class InventoryDao extends AbstractBasicDao<Product> implements Dao<Product> {

   /**
    * Debits the central inventory balanced.
    *
    * todo not exactly sure what the implications of overdrawing is
    *    - e.g. is it physically possible? if so, should it be enforced here?
    */
   public void debitBalance(String productCode, Double amountToDebit) {

      for (Product product : findAll()) {
         if (product.getCode().equals(productCode)) {
            product.setBalance(product.getBalance() - amountToDebit);
         }
      }
   }

   public boolean isAmountAvailable(String code, double applied) {
      Product product = findByCode(code);

      if (product != null) {
         if (product.getBalance() >= applied) {
            return true;
         }
      }

      return false;
   }

   private Product findByCode(String code) {

      Product prod = null;

      for (Product product : findAll()) {
         if (product.getCode().equals(code)) {
            prod = product;
            break;
         }
      }

      return prod;
   }
}