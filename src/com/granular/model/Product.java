package com.granular.model;

public class Product {
   private String name;
   private String code;
   private Double balance;
   private String unitType;

   public Product(String name, String code, Double balance, String unitType) {
      this.name = name;
      this.balance = balance;
      this.code = code;
      this.unitType = unitType;
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

   public String getUnitType() {
      return unitType;
   }
}