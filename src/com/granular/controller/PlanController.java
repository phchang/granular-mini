package com.granular.controller;

import com.granular.dao.InventoryDao;
import com.granular.dao.PlanDao;
import com.granular.exception.ValidationException;
import com.granular.model.*;

import java.util.*;

public class PlanController {

   private PlanDao planDao;
   private InventoryDao inventoryDao;

   private Plan selectedPlan;

   public PlanController(InventoryDao inventoryDao) {
      planDao = new PlanDao();
      this.inventoryDao = inventoryDao;

      selectedPlan = null;
   }

   public Plan getSelectedPlan() {
      return selectedPlan;
   }

   public Iterable<Plan> getPlans() {
      return planDao.findAll();
   }

   public void addPlan(Plan plan) {
      selectedPlan = plan;

      plan.setId(planDao.getNextId());
      planDao.save(plan);
   }

   public Iterable<Product> getProducts() {
      return inventoryDao.findAll();
   }

   public void addProductsToInventory(List<Product> products) {
      for (Product product : products) {
         inventoryDao.save(product);
      }
   }

   public void removeProduct(Product product) {
      // todo validate not used by any other work plans, or zero balance
   }

   public void addWorkOrder(Task task, WorkOrder orderToAdd) throws ValidationException {
      // validate task
      List<WorkOrder> workOrders = task.getWorkOrders();

      Double summedTargetQuantity = 0d;

      if (workOrders != null) {
         for (WorkOrder workOrder : workOrders) {
            summedTargetQuantity += workOrder.getTargetQuantity();
         }
      }

      if ((summedTargetQuantity + orderToAdd.getTargetQuantity()) > task.getTargetQuantity()) {
         throw new ValidationException("The target quantities of the work orders exceeds the task's target quantity.");
      } else {
         task.addWorkOrder(orderToAdd);
         orderToAdd.setTask(task);
      }
   }

   public void addTaskToPlan(Plan plan, Task task) {
      plan.getTasks().add(task);
      task.setTaskStatus(Status.NOT_STARTED);
      task.setPlan(plan);
   }

   public void updateWorkOrderStatus(WorkOrder workOrder, Status status) throws ValidationException {

      boolean isAvailableInInventory = inventoryDao.isAmountAvailable(workOrder.getTask().getTargetProduct().getCode(), workOrder.getTargetQuantity());

      if (!isAvailableInInventory) {
         throw new ValidationException("Not enough inventory available.");
      }

      workOrder.setWorkOrderStatus(status);

      // update status of Task
      Task task = workOrder.getTask();

      // todo move this into the Task (refreshStatus())
      List<WorkOrder> workOrders = task.getWorkOrders();

      Double appliedAmount = 0d;
      Set<Status> statusSet = new HashSet<>();
      for (WorkOrder order : workOrders) {
         statusSet.add(order.getWorkOrderStatus());
         appliedAmount += order.getActualQuantityApplied();
      }

      if (statusSet.isEmpty()) {
         task.setTaskStatus(Status.NOT_STARTED);
      } else if (statusSet.size() == 1) {
         // if 1, all tasks must be in the same status

         Status previousStatus = task.getTaskStatus();
         Status workOrderStatus = statusSet.iterator().next();

         task.setTaskStatus(workOrderStatus);

         if (!previousStatus.equals(Status.COMPLETED) && workOrderStatus.equals(Status.COMPLETED)) {
            // todo when task completed, update inventory
            inventoryDao.debitBalance(task.getTargetProduct().getCode(), appliedAmount);
         }

      } else {
         // otherwise
         task.setTaskStatus(Status.IN_PROGRESS);
      }


   }

   public void updateWorkOrderActual(WorkOrder workOrder, double applied) {
      workOrder.setActualQuantityApplied(applied);
   }

   public void updateWorkOrderTarget(WorkOrder workOrder, double target) {
      workOrder.setTargetQuantity(target);
   }

   public void removePlan(Plan plan) {
      planDao.remove(plan.getId());
   }

   public void removeTask(Task task) {
      List<Task> tasks = task.getPlan().getTasks();
      tasks.remove(task);
   }

   public void removeWorkOrder(WorkOrder workOrder) {
      List<WorkOrder> workOrders = workOrder.getTask().getWorkOrders();
      workOrders.remove(workOrder);
   }


   public void updateTaskTarget(Task task, Double target) {
      task.setTargetQuantity(target);
   }

   public List<ProductReport> getActualsReport(Plan plan) {
      List<ProductReport> targetVsActuals = new ArrayList<>();

      List<Task> tasks = plan.getTasks();

      for (Task task : tasks) {
         Product product = task.getTargetProduct();

         Double totalApplied = 0d;

         List<WorkOrder> workOrders = task.getWorkOrders();
         for (WorkOrder workOrder : workOrders) {
            Double actualQuantityApplied = workOrder.getActualQuantityApplied();
            totalApplied += actualQuantityApplied;
         }

         ProductReport report = new ProductReport(task.getTargetProduct(), task.getTargetQuantity(), totalApplied);
         targetVsActuals.add(report);
      }

      return targetVsActuals;
   }
}