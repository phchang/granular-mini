package com.granular.controller;

import com.granular.dao.impl.InventoryDao;
import com.granular.dao.impl.PlanDao;
import com.granular.exception.ValidationException;
import com.granular.model.*;

import java.util.*;

/**
 * Handles operations operations and logic regarding plans, tasks, and work orders.
 *
 * todo perhaps these calls should be grouped into various services (e.g. Plan, Inventory, etc.)
 */
public class PlanController {

   private PlanDao planDao;
   private InventoryDao inventoryDao;

   public PlanController(InventoryDao inventoryDao) {

      planDao = new PlanDao(); // this would be injected in if it read and wrote to a real DB
      this.inventoryDao = inventoryDao;

   }

   public Iterable<Plan> getPlans() {
      return planDao.findAll();
   }

   public void addPlan(Plan plan) {

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

   /**
    * The work order being added must have a target quantity, when added to the sum of existing target quantities, that
    * is less than the task's target. For example, the task's target is 10, and 2 work orders are added with values
    * 5 and 6 (totalling 11). If this is the case, a ValidationException is thrown.
    *
    * @param task the task that the order should be added to
    * @param orderToAdd the order's target quantity, added to the existing sum of target quantities must be less than
    *                   the task's target quantity
    * @throws ValidationException
    */
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

   /**
    * When a task is added to a plan, the status is defaulted to NOT_STARTED.
    */
   public void addTaskToPlan(Plan plan, Task task) {

      plan.getTasks().add(task);
      task.setTaskStatus(Status.NOT_STARTED);
      task.setPlan(plan);

   }

   /**
    * When updating a work order's status, the amount of actual inventory must exist for the product and quantity
    * specified in the work order. If there is insufficient inventory, an exception is thrown.
    *
    * Based on the status of the work orders, the task's status will be updated accordingly. Furthermore if a
    * task is set to completed, the actual quantity applied in the work orders will be debited from the central
    * repository.
    *
    * @throws ValidationException
    */
   public void updateWorkOrderStatus(WorkOrder workOrder, Status status) throws ValidationException {

      // validate inventory
      boolean isAvailableInInventory = inventoryDao.isAmountAvailable(workOrder.getTask().getTargetProduct().getCode(), workOrder.getTargetQuantity());

      if (!isAvailableInInventory) {
         throw new ValidationException("Not enough inventory available.");
      }

      workOrder.setWorkOrderStatus(status);

      // update status of Task
      Task task = workOrder.getTask();

      List<WorkOrder> workOrders = task.getWorkOrders();

      Double appliedAmount = 0d;
      Set<Status> statusSet = new HashSet<>();
      for (WorkOrder order : workOrders) {
         statusSet.add(order.getWorkOrderStatus());
         appliedAmount += order.getActualQuantityApplied();
      }

      // if there are no work orders, it can be assumed the task is NOT_STARTED
      if (statusSet.isEmpty()) {
         task.setTaskStatus(Status.NOT_STARTED);
      } else if (statusSet.size() == 1) {

         // if 1, all tasks must be in the same status
         Status previousStatus = task.getTaskStatus();
         Status workOrderStatus = statusSet.iterator().next();

         task.setTaskStatus(workOrderStatus);

         // if the status was not COMPLETED, and now it becomes COMPLETED, debit the balance from the inventory
         if (!previousStatus.equals(Status.COMPLETED) && workOrderStatus.equals(Status.COMPLETED)) {
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