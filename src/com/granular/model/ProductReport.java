package com.granular.model;

public class ProductReport {
   private Product product;
   private Double target;
   private Double actual;

   public ProductReport(Product product, Double target, Double actual) {
      this.product = product;
      this.target = target;
      this.actual = actual;
   }

   public Product getProduct() {
      return product;
   }

   public Double getTarget() {
      return target;
   }

   public Double getActual() {
      return actual;
   }
}