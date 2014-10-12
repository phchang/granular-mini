package com.granular.model;

import java.util.List;

public class Inventory {
   private List<Product> products;

   public Inventory() {
   }

   public List<Product> getProducts() {
      return products;
   }

   public void setProducts(List<Product> products) {
      this.products = products;
   }
}
