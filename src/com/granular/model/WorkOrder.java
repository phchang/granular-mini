package com.granular.model;

public class WorkOrder {
   private Double targetQuantity;
   private Double actualQuantityApplied;
   private Status workOrderStatus;

   public WorkOrder() {
   }

   public Double getTargetQuantity() {
      return targetQuantity;
   }

   public void setTargetQuantity(Double targetQuantity) {
      this.targetQuantity = targetQuantity;
   }

   public Double getActualQuantityApplied() {
      return actualQuantityApplied;
   }

   public void setActualQuantityApplied(Double actualQuantityApplied) {
      this.actualQuantityApplied = actualQuantityApplied;
   }

   public Status getWorkOrderStatus() {
      return workOrderStatus;
   }

   public void setWorkOrderStatus(Status workOrderStatus) {
      this.workOrderStatus = workOrderStatus;
   }
}
