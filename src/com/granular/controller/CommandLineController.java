package com.granular.controller;

import com.granular.exception.ValidationException;
import com.granular.model.*;

import java.io.*;
import java.util.*;

/**
 * CommandLineController serves to take input and display output to the user, delegating logic to the PlanController.
 */
public class CommandLineController {
   private PrintStream out;

   private PlanController controller;
   private boolean running;

   private BufferedReader reader;
   private Stack<View> viewStack;

   public CommandLineController(PlanController controller, InputStream in, PrintStream out) {
      this.out = out;
      this.controller = controller;
      this.reader = new BufferedReader(new InputStreamReader(in));
      this.viewStack = new Stack<>();
   }

   public void start() {
      running = true;

      out.println("\nCrop Planner");
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
               // a null view serves as an instruction to go "back"
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
               out.println("\nPlan name: ");
               String planName = reader.readLine();

               Plan plan = new Plan();
               plan.setName(planName);

               controller.addPlan(plan);

               return new PlanEditView(plan);
            case "L":
               printProducts(controller.getProducts());
               out.println("");
               break;
            case "Q":
               running = false;
               break;
         }

         // for a lack of a better way to handle this, if the user types in something invalid, just show the view again
         return this;
      }
   }

   private class PlanSelectView implements View {

      @Override
      public View show() throws IOException {
         Iterable<Plan> plans = controller.getPlans();
         Map<Long, Plan> planMap = new HashMap<>();

         String header = "Select a plan";
         out.println("\n" + header);
         printHeaderLine(header.length());

         for (Plan plan : plans) {

            planMap.put(plan.getId(), plan);
            int numTasks = plan.getTasks() == null ? 0 : plan.getTasks().size();
            String suffix = numTasks == 1 ? "tasks" : "tasks"; // don't display "task(s)", drawing a line in the sand

            out.println("[" + plan.getId() + "]" + "\t" + plan.getName() + " (" + numTasks + " " + suffix + ")");

         }

         out.println("\n(B)ack");

         String option = reader.readLine();

         switch (option) {
            case "B":
               return null;
            default:
               try {
                  Long planId = Long.valueOf(option);
                  Plan plan = planMap.get(planId);

                  if (plan != null) {
                     return new PlanEditView(plan);
                  }
               } catch (NumberFormatException e) {
                  // no-op
               }
         }

         return this;
      }
   }

   private class PlanEditView implements View {

      private Plan plan;

      private PlanEditView(Plan plan) {
         this.plan = plan;
      }

      @Override
      public View show() throws IOException {
         int numTasks = plan.getTasks().size();
         String suffix = numTasks == 1 ? "task" : "tasks";
         String header = plan.getName() + " (" + numTasks + " " + suffix + ")"; // e.g. Plan A (2 tasks)

         out.println("\n" + header);
         printHeaderLine(header.length());

         if (plan.getTasks() == null || plan.getTasks().isEmpty()) {

            out.println("There are no tasks.");
            out.println("\n(A)dd Task, (B)ack, (D)elete Plan");

         } else {
            // print out task selection options
            for (int i = 0; i < plan.getTasks().size(); i++) {
               Task task = plan.getTasks().get(i);

               out.println("[" + i + "] " + task.getTargetProduct().getCode() + "\t" + task.getTaskStatus() + "\t" + task.getTargetQuantity() + " " + task.getTargetProduct().getUnitType());
            }

            // generate a target vs. actuals report
            List<ProductReport> actualsReport = controller.getActualsReport(plan);

            out.println("\n----- target vs. actuals -----");
            for (ProductReport productReport : actualsReport) {
               String code = productReport.getProduct().getCode();
               Double actual = productReport.getActual();
               Double target = productReport.getTarget();

               out.println("\t" + code + "\t" + target + "\t" + actual);
            }

            out.println("\n(A)dd Task, (B)ack, (D)elete Plan, or Select Task by Number");
         }

         String option = reader.readLine();

         switch (option) {
            case "A":
               Task task = new Task();

               out.println("");
               Map<String, Product> productMap = printProducts(controller.getProducts());

               out.println("\nProduct code:");
               option = reader.readLine();

               // don't want to store a reference
               Product product = productMap.get(option);
               task.setTargetProduct(new Product(product.getName(), product.getCode(), product.getBalance(), product.getUnitType()));

               out.println("Target quantity: ");
               option = reader.readLine();
               task.setTargetQuantity(Double.parseDouble(option));

               controller.addTaskToPlan(plan, task);

               return this;
            case "D":
               // todo does removing a plan decrement the inventory balance?
               controller.removePlan(plan);
               return null;

            case "B":
               return null;

            default:
               try {
                  Integer taskId = Integer.parseInt(option);

                  if (taskId >= 0 && taskId < plan.getTasks().size()) {
                     return new TaskEditView(plan.getTasks().get(taskId));
                  }
               } catch (NumberFormatException e) {
                  // no-op
               }

            return this;
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

         String header = task.getPlan().getName() + " --> " + task.getTargetProduct().getCode() + "\t" + task.getTaskStatus() + "\t" + task.getTargetQuantity() + " " + task.getTargetProduct().getUnitType();
         out.println("\n" + header);
         printHeaderLine(header.length());

         if (task.getWorkOrders() == null || task.getWorkOrders().isEmpty()) {
            out.println("There are no work orders for this task.");
            out.println("\n(U)pdate Target, (A)dd Work Order, (D)elete Task, (B)ack");
         } else {
            printWorkOrders(task.getWorkOrders());
            out.println("\n(U)pdate Target, (A)dd Work Order, (D)elete Task, (B)ack, or Select Work Order By Number");
         }

         String option = reader.readLine();

         switch (option) {
            case "A":
               WorkOrder workOrder = new WorkOrder();

               // show available products
               out.println("Target quantity:");
               option = reader.readLine();
               workOrder.setTargetQuantity(Double.valueOf(option));

               try {
                  controller.addWorkOrder(task, workOrder);
               } catch (ValidationException e) {
                  // the summed quantities of the work orders exceeds the target of the task
                  out.println("The work order could not be added.");
                  out.println(">>>> " + e.getMessage());
                  out.println("Press enter to continue...");
                  reader.readLine();
               }

               return this;

            case "D":
               controller.removeTask(task);
               return null;

            case "U":
               out.println("Enter a new target quantity:");
               option = reader.readLine();

               try {
                  Double target = Double.parseDouble(option);
                  controller.updateTaskTarget(task, target);
               } catch (NumberFormatException e) {
                  // no-op
               }

               return this;

            case "B":
               return null;

            default:
               try {
                  Integer wo = Integer.parseInt(option);

                  if (wo >= 0 && wo < task.getWorkOrders().size()) {
                     return new WorkOrderEditView(task.getWorkOrders().get(wo));
                  }
               } catch (NumberFormatException e) {
                  // no-op
               }

            return this;
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
         String unitType = workOrder.getTask().getTargetProduct().getUnitType();

         String planName = workOrder.getTask().getPlan().getName();

         String header = planName + " --> " + status + "\t" + targetQuantity + "\t" + actualQuantityApplied + " " + unitType;
         out.println("\n" + header);
         printHeaderLine(header.length());

         out.println("(U)pdate Applied, Update (T)arget, (C)hange Status, (D)elete Work Order, (B)ack");

         String option = reader.readLine();

         switch (option) {
            case "U":
               out.println("\nEnter amount applied:");
               option = reader.readLine();

               try {
                  double applied = Double.parseDouble(option);
                  controller.updateWorkOrderActual(workOrder, applied);
               } catch (NumberFormatException e) {
                  // no-op
               }

               return this;

            case "T":
               out.println("\nEnter new target:");
               option = reader.readLine();

               try {
                  double target = Double.parseDouble(option);
                  controller.updateWorkOrderTarget(workOrder, target);
               } catch (NumberFormatException e) {
                  // no-op
               }

               return this;

            case "C":
               out.println("");

               for (int i = 0; i < Status.values().length; i++) {
                  Status s = Status.values()[i];
                  out.println("[" + i + "] " + s.toString());
               }

               option = reader.readLine();

               try {
                  Integer statusOption = Integer.parseInt(option);
                  controller.updateWorkOrderStatus(workOrder, Status.values()[statusOption]);

               } catch (NumberFormatException e) {
                  // no-op
               } catch (ValidationException e) {
                  out.println("Could not update status of work order status.");
                  out.println(">>>> " + e.getMessage());
                  out.println("Press enter to continue...");
                  reader.readLine();
               }

               return this;
            case "D":
               controller.removeWorkOrder(workOrder);
               return null;
            case "B":
               return null;
         }

         return this;
      }
   }

   private Map<String, Product> printProducts(Iterable<Product> products) {
      Map<String, Product> codeToProductMap = new HashMap<>();

      out.println("Product Inventory:");

      for (Product product : products) {
         codeToProductMap.put(product.getCode(), product);
         out.println("\t[" + product.getCode() + "]\t" + product.getBalance() + " " + product.getUnitType() + "\t" + product.getName());
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
         String unitType = workOrder.getTask().getTargetProduct().getUnitType();
         out.println("[" + i + "] " + workOrder.getWorkOrderStatus() + "\t" + workOrder.getTargetQuantity() + "\t" + workOrder.getActualQuantityApplied() + " " + unitType);
      }
   }

   private void printHeaderLine(int length) {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < length; i++) {
         builder.append("-");
      }

      out.println(builder.toString());
   }
}