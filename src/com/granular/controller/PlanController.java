package com.granular.controller;

import com.granular.dao.InventoryDao;
import com.granular.dao.PlanDao;
import com.granular.model.Plan;
import com.granular.model.Product;

import java.util.List;

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
}
