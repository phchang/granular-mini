package com.granular;

import com.granular.controller.CommandLineController;
import com.granular.controller.PlanController;
import com.granular.dao.impl.InventoryDao;
import com.granular.model.Product;

import java.util.ArrayList;
import java.util.List;

public class Main {

   public static void main(String[] args) {

      List<Product> products = new ArrayList<>();
      products.add(new Product("Herbicide 1", "HB1", 100d, "gal"));
      products.add(new Product("Herbicide 2", "HB2", 200d, "gal"));
      products.add(new Product("Food 1", "FD1", 1000d, "lbs"));
      products.add(new Product("Food 2", "FD2", 2000d, "lbs"));

      InventoryDao inventoryDao = new InventoryDao();

      PlanController controller = new PlanController(inventoryDao);
      controller.addProductsToInventory(products);

      CommandLineController clController = new CommandLineController(controller, System.in, System.out);
      clController.start();
   }
}