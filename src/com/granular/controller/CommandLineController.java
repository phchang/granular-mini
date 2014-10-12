package com.granular.controller;

import com.granular.dao.InventoryDao;
import com.granular.model.*;

import java.io.*;
import java.util.*;

public class CommandLineController {
   private PrintStream out;

   private PlanController controller;
   private boolean running;

   private BufferedReader reader;
   private Stack<View> viewStack;

   public CommandLineController(PlanController controller, InputStream in, PrintStream out) {
      this.controller = controller;
      this.out = out;
      this.reader = new BufferedReader(new InputStreamReader(in));
      this.viewStack = new Stack<>();
   }

   public void start() {
      running = true;

      out.println("Crop Planner");
      out.println("------------\n");

      View homeView = new HomeView();
      View currentView = homeView;

      while (running) {
         try {
            View nextView = currentView.show();

            if (nextView != null) {
               if (currentView != nextView) {
                  viewStack.push(currentView);
               }
               currentView = nextView;
            } else {
               currentView = viewStack.isEmpty() ? homeView : viewStack.pop();
            }

         } catch (IOException e) {
            out.println("An error occurred reading from the command line! Exiting");
            running = false;
         }
      }
   }

   private interface View { public View show() throws IOException; }

   private class HomeView implements View {
      @Override
      public View show() throws IOException {
         out.println("(V)iew Plans, (A)dd Plan, (L)ist Inventory, (Q)uit");

         String option = reader.readLine();

         switch (option) {
            case "V":
               // display a list of available plans

               Iterable<Plan> plans = controller.getPlans();

               if (!plans.iterator().hasNext()) {
                  out.println("There are currently no plans.\n");
                  return null;
               }

               return new PlanSelectView();

            case "A":
               out.println("");
               out.println("Plan name: ");
               String planName = reader.readLine();

               Plan plan = new Plan();
               plan.setName(planName);

               controller.addPlan(plan);

               return new PlanEditView(plan);
            case "L":
               printProducts(controller.getProducts());
               break;
            case "Q":
               running = false;
               break;
         }


         return null;
      }
   }

   private class PlanSelectView implements View {

      @Override
      public View show() throws IOException {
         Iterable<Plan> plans = controller.getPlans();
         Map<Long, Plan> planMap = new HashMap<>();

         out.println("\nSelect a plan");
         out.println("--------------");

         for (Plan plan : plans) {
            planMap.put(plan.getId(), plan);
            int numTasks = plan.getTasks() == null ? 0 : plan.getTasks().size();
            String suffix = numTasks == 1 ? "tasks" : "tasks";

            out.println("[" + plan.getId() + "]" + "\t" + plan.getName() + " (" + numTasks + " " + suffix + ")");
         }

         out.println("(B)ack");

         String option = reader.readLine();

         if (option.equals("B")) {
            return null;
         }

         return new PlanEditView(planMap.get(Long.valueOf(option)));
      }
   }

   private class PlanEditView implements View {
      private Plan plan;

      private PlanEditView(Plan plan) {
         this.plan = plan;
      }

      @Override
      public View show() throws IOException {

         out.println("\n" + plan.getName());
         out.println("------------------");

         if (plan.getTasks() == null || plan.getTasks().isEmpty()) {
            out.println("There are no tasks.\n");
            out.println("(A)dd Task, (B)ack");
         } else {

            for (int i = 0; i < plan.getTasks().size(); i++) {
               Task task = plan.getTasks().get(i);

               out.println("[" + i + "] " + task.getTargetProduct().getCode() + "\t" + task.getTaskStatus() + "\t" + task.getTargetQuantity());
            }

            out.println("(A)dd Task, (B)ack, Select Task by [Number], ");
         }

         String option = reader.readLine();

         switch (option) {
            case "A":

               Task task = new Task();

               Map<String, Product> productMap = printProducts(controller.getProducts());

               out.println("Product code:");
               option = reader.readLine();

               // don't want to store a reference
               Product product = productMap.get(option);
               task.setTargetProduct(new Product(product.getName(), product.getCode(), product.getBalance()));

               out.println("Target quantity: ");
               option = reader.readLine();
               task.setTargetQuantity(Double.parseDouble(option));

               plan.addTask(task);

               return this;
            case "B":
               return null;
            default:
               Integer taskId = Integer.parseInt(option); // todo handle exception

               return new TaskEditView(plan.getTasks().get(taskId));
         }
      }
   }

   private class TaskEditView implements View {

      private Task task;

      private TaskEditView(Task task) {
         this.task = task;
      }

      @Override
      public View show() throws IOException {

         out.println(task.getTargetProduct().getCode() + "\t" + task.getTaskStatus() + "\t" + task.getTargetQuantity());

         if (task.getWorkOrders() == null || task.getWorkOrders().isEmpty()) {
            out.println("There are no work orders for this task.");
            out.println("(A)dd Work Order, (B)ack");
         } else {
            printWorkOrders(task.getWorkOrders());
            out.println("(A)dd Work Order, (B)ack, or Select Work Order By Number");
         }

         String option = reader.readLine();

         switch (option) {
            case "A":

               WorkOrder workOrder = new WorkOrder();

               // show available products
               out.println("Target quantity:");
               option = reader.readLine();
               workOrder.setTargetQuantity(Double.valueOf(option));
               task.addWorkOrder(workOrder);

               return this;

            case "B":
               return null;
            default: 
               Integer wo = Integer.parseInt(option);
               return new WorkOrderEditView(task.getWorkOrders().get(wo));
         }
      }
   }

   private class WorkOrderEditView implements View {

      private WorkOrder workOrder;

      private WorkOrderEditView(WorkOrder workOrder) {
         this.workOrder = workOrder;
      }

      @Override
      public View show() throws IOException {

         Status status = workOrder.getWorkOrderStatus();
         Double targetQuantity = workOrder.getTargetQuantity();
         Double actualQuantityApplied = workOrder.getActualQuantityApplied();

         out.println("\n" + status + "\t" + targetQuantity + "\t" + actualQuantityApplied);

         out.println("(U)pdate Applied, (C)hange Status, (B)ack");

         String option = reader.readLine();

         switch (option) {
            case "U":

               out.println("\nEnter amount applied:");
               option = reader.readLine();

               double applied = Double.parseDouble(option);

               workOrder.setActualQuantityApplied(applied);

               return this;

            case "C":

               out.println("");

               for (int i = 0; i < Status.values().length; i++) {
                  Status s = Status.values()[i];
                  out.println("[" + i + "] " + s.toString());
               }

               option = reader.readLine();

               Integer statusOption = Integer.parseInt(option);

               workOrder.setWorkOrderStatus(Status.values()[statusOption]);

               return this;
            case "B":
               return null;
         }

         return null;
      }
   }

   private Map<String, Product> printProducts(Iterable<Product> products) {
      Map<String, Product> codeToProductMap = new HashMap<>();

      out.println("Product Inventory:");

      for (Product product : products) {
         codeToProductMap.put(product.getCode(), product);
         out.println("\t" + product.getCode() + "\t" + product.getBalance() + "\t" + product.getName());
      }

      return codeToProductMap;
   }

   private void printWorkOrders(Iterable<WorkOrder> workOrders) {
      List<WorkOrder> workOrderList = new ArrayList<>();
      
      out.println("Work Orders:");

      for (WorkOrder workOrder : workOrders) {
         workOrderList.add(workOrder);
      }

      for (int i = 0; i < workOrderList.size(); i++) {
         WorkOrder workOrder = workOrderList.get(i);
         out.println("[" + i + "] " + workOrder.getWorkOrderStatus() + "\t" + workOrder.getTargetQuantity() + "\t" + workOrder.getActualQuantityApplied());
      }
   }
}
