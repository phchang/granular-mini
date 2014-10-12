package com.granular.controller;

import com.granular.dao.InventoryDao;
import com.granular.dao.PlanDao;
import com.granular.model.Plan;
import com.granular.model.Product;

import java.sql.SQLException;
import java.util.List;

public class PlanController {

   private PlanDao planDao;
   private InventoryDao inventoryDao;

   private Plan selectedPlan;

   public PlanController(InventoryDao inventoryDao) {
//      planDao = new PlanDao();
      planDao = null; // todo fix
      this.inventoryDao = inventoryDao;

      selectedPlan = null;
   }

   public Plan getSelectedPlan() {
      return selectedPlan;
   }

   public Iterable<Plan> getPlans() {
      try {
         return planDao.findAll();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return null;
   }

   public void addPlan(Plan plan) {
      selectedPlan = plan;

      plan.setId(planDao.getNextId());
//      planDao.save(plan);
   }

   public Iterable<Product> getProducts() {
      try {
         return inventoryDao.findAll();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return null;
   }

   public void addProductsToInventory(List<Product> products) {
      for (Product product : products) {
//         inventoryDao.save(product);
      }
   }
}
