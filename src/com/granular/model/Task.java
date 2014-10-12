package com.granular.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
   private Double targetQuantity;
   private Product targetProduct; // todo consider just storing code here
   private Status taskStatus; // todo inherit status from work orders
   private List<WorkOrder> workOrders;

   public Task() {
      this.workOrders = new ArrayList<>();
   }

   public Double getTargetQuantity() {
      return targetQuantity;
   }

   public void setTargetQuantity(Double targetQuantity) {
      this.targetQuantity = targetQuantity;
   }

   public Product getTargetProduct() {
      return targetProduct;
   }

   public void setTargetProduct(Product targetProduct) {
      this.targetProduct = targetProduct;
   }

   public Status getTaskStatus() {
      return taskStatus;
   }

   public void setTaskStatus(Status taskStatus) {
      this.taskStatus = taskStatus;
   }

   public List<WorkOrder> getWorkOrders() {
      return workOrders;
   }

   public void setWorkOrders(List<WorkOrder> workOrders) {
      this.workOrders = workOrders;
   }

   public void addWorkOrder(WorkOrder workOrder) {
      workOrder.setWorkOrderStatus(Status.NOT_STARTED);
      this.workOrders.add(workOrder);
   }
}