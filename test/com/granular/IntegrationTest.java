package com.granular;

import com.granular.controller.PlanController;
import com.granular.dao.InventoryDao;
import com.granular.model.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.*;

public class IntegrationTest {

   @Test
   public void testHappyPath() {

      // todo include units? have different subtypes:
      Product product1 = new Product("Herbicide 1", "HB1", 100d);
      Product product2 = new Product("Herbicide 2", "HB2", 200d);
      Product product3 = new Product("Food 1", "FD1", 1000d);
      Product product4 = new Product("Food 2", "FD2", 2000d);

      InventoryDao inventoryDao = new InventoryDao();
      inventoryDao.save(product1);
      inventoryDao.save(product2);
      inventoryDao.save(product3);
      inventoryDao.save(product4);

      PlanController controller = new PlanController(inventoryDao);

      Iterable<Plan> plans = controller.getPlans();

      assertFalse("There should be no plans", plans.iterator().hasNext());

      // add a plan
      Plan plan1 = new Plan();
      plan1.setId(1l);
      plan1.setName("Plan 1");

      controller.addPlan(plan1);

      // todo not sure if the auto-selection is appropriate here
      assertEquals(Long.valueOf(1l), controller.getSelectedPlan().getId());

      // add a task
      Task task1 = new Task();
      task1.setTargetQuantity(100d);
      task1.setTargetProduct(product1);
      task1.setTaskStatus(Status.NOT_STARTED);

      controller.getSelectedPlan().addTask(task1);

      // add a work order
      WorkOrder workOrder1 = new WorkOrder();
      workOrder1.setTargetQuantity(10d);
      workOrder1.setWorkOrderStatus(Status.NOT_STARTED);

      // todo must validate somehow that this is a valid quantity, throw exception otherwise?
      task1.addWorkOrder(workOrder1);


      ///////

      plans = controller.getPlans();

      int planCount = 0;

      for (Plan plan : plans) {
         planCount++;
      }

      assertEquals(1, planCount);
   }
}


