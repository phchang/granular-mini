package com.granular.model;

public class Product {
   private String name;
   private Double balance;

   public Product(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public Double getBalance() {
      return balance;
   }

   public void setBalance(Double balance) {
      this.balance = balance;
   }
}
