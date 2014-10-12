package com.granular.model;

public class Product {
   private String name;
   private String code;
   private Double balance;

   public Product(String name, String code, Double balance) {
      this.name = name;
      this.balance = balance;
      this.code = code;
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

   public String getCode() {
      return code;
   }
}
