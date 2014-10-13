package com.granular.controller;

import com.granular.dao.impl.InventoryDao;
import com.granular.exception.ValidationException;
import com.granular.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class PlanControllerTest {

   private InventoryDao inventoryDao;
   private List<Product> products = new ArrayList<>();

   @Before
   public void setUp() throws Exception {
      Product product1 = new Product("Herbicide 1", "HB1", 100d, "gal");
      Product product2 = new Product("Herbicide 2", "HB2", 200d, "gal");
      Product product3 = new Product("Food 1", "FD1", 1000d, "lbs");
      Product product4 = new Product("Food 2", "FD2", 2000d, "lbs");

      products.add(product1);
      products.add(product2);
      products.add(product3);
      products.add(product4);

      inventoryDao = new InventoryDao();
      inventoryDao.save(product1);
      inventoryDao.save(product2);
      inventoryDao.save(product3);
      inventoryDao.save(product4);
   }

   @Test
   public void testHappyPath() {

      PlanController controller = new PlanController(inventoryDao);

      Iterable<Plan> plans = controller.getPlans();

      Iterator<Plan> iterator = plans.iterator();
      assertFalse("There should be no plans", iterator.hasNext());

      // add a plan
      Plan plan1 = new Plan();
      plan1.setId(1l);
      plan1.setName("Plan 1");

      controller.addPlan(plan1);

      plans = controller.getPlans();
      iterator = plans.iterator();

      assertTrue("There should be 1 plan.", iterator.hasNext());
      Plan plan = iterator.next();
      assertFalse("And only 1 plan.", iterator.hasNext());

      // add a task
      Task task1 = new Task();
      task1.setTargetQuantity(100d);
      task1.setTargetProduct(products.get(1));

      controller.addTaskToPlan(plan, task1);

      plan = controller.getPlans().iterator().next();

      List<Task> tasks = plan.getTasks();
      assertEquals(1, tasks.size());
      assertEquals("The status should default to NOT_STARTED", Status.NOT_STARTED, tasks.get(0).getTaskStatus());

      // add a work order
      WorkOrder workOrder1 = new WorkOrder();
      workOrder1.setTargetQuantity(10d);
      workOrder1.setWorkOrderStatus(Status.NOT_STARTED);

      try {
         controller.addWorkOrder(plan.getTasks().get(0), workOrder1);
      } catch (ValidationException e) {
         fail("exception shouldn't be thrown here, this work order is valid");
      }
   }

   @Test
   public void testAddWorkOrder_TargetQuantityTooHigh() {

      PlanController controller = new PlanController(inventoryDao);

      Plan plan1 = new Plan();
      plan1.setId(1l);
      plan1.setName("Plan 1");

      controller.addPlan(plan1);

      Task task1 = new Task();
      task1.setTargetQuantity(100d);
      task1.setTargetProduct(products.get(1));

      controller.addTaskToPlan(plan1, task1);

      WorkOrder workOrder1 = new WorkOrder();
      workOrder1.setTargetQuantity(2000d);
      workOrder1.setWorkOrderStatus(Status.NOT_STARTED);

      try {
         controller.updateWorkOrderStatus(workOrder1, Status.IN_PROGRESS);
         fail("An exception should have been thrown here, the target quantity is greater than what is in the inventory");
      } catch (ValidationException e) {
         // no-op
      }

      Iterable<Plan> plans = controller.getPlans();
      Plan p = plans.iterator().next();

      List<Task> tasks = p.getTasks();
      Task task = tasks.get(0);

      assertTrue(task.getWorkOrders().isEmpty());
   }

   @Test
   public void testUpdateWorkOrderStatus() {

      PlanController controller = new PlanController(inventoryDao);

      Plan plan1 = new Plan();
      plan1.setId(1l);
      plan1.setName("Plan 1");

      controller.addPlan(plan1);

      Task task1 = new Task();
      task1.setTargetQuantity(100d);
      task1.setTargetProduct(products.get(1));

      controller.addTaskToPlan(plan1, task1);

      WorkOrder workOrder1 = new WorkOrder();
      workOrder1.setTargetQuantity(75d);
      workOrder1.setWorkOrderStatus(Status.NOT_STARTED);

      try {
         controller.addWorkOrder(task1, workOrder1);
      } catch (ValidationException e) {
         fail("this is a valid work order, should not fail");
      }

      inventoryDao.debitBalance("HB1", 99d);

      try {
         controller.addWorkOrder(task1, workOrder1);
         fail("An exception should have been thrown here, the target quantity is too high");
      } catch (ValidationException e) {
         // no-op
      }

      Iterable<Plan> plans = controller.getPlans();
      Plan p = plans.iterator().next();

      List<Task> tasks = p.getTasks();
      Task task = tasks.get(0);


      assertEquals(1, task.getWorkOrders().size());
      assertEquals(Status.NOT_STARTED, task.getWorkOrders().get(0).getWorkOrderStatus());
   }
}


