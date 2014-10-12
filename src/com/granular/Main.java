package com.granular;

import com.granular.controller.CommandLineController;
import com.granular.controller.PlanController;
import com.granular.dao.InventoryDao;
import com.granular.dao.PlanDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

   public static void main(String[] args) throws SQLException {

        /*
            todo welcome screen
            todo: plans
               - list plans
               - create plans
               - add tasks to plan
               - assign work orders to each task

            todo: tasks
               - add tasks

            todo: work orders
               - add work order
         */

      Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:db/granular", "sa", "");

      InventoryDao inventoryDao = new InventoryDao(conn);
      PlanDao planDao = new PlanDao(conn);

      PlanController controller = new PlanController(inventoryDao);

      CommandLineController clController = new CommandLineController(controller, System.in, System.out);
      clController.start();
   }
}