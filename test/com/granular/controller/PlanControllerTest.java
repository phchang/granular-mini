package com.granular.controller;

import com.granular.controller.PlanController;
import com.granular.dao.InventoryDao;
import com.granular.exception.ValidationException;
import com.granular.model.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class PlanControllerTest {

   @Test
   public void testHappyPath() {

      Product product1 = new Product("Herbicide 1", "HB1", 100d, "gal");
      Product product2 = new Product("Herbicide 2", "HB2", 200d, "gal");
      Product product3 = new Product("Food 1", "FD1", 1000d, "lbs");
      Product product4 = new Product("Food 2", "FD2", 2000d, "lbs");

      InventoryDao inventoryDao = new InventoryDao();
      inventoryDao.save(product1);
      inventoryDao.save(product2);
      inventoryDao.save(product3);
      inventoryDao.save(product4);

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
      task1.setTargetProduct(product1);

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
}


