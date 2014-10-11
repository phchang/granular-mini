# Mini Granular Module
This project involves implementing a small console application that allows the user to define a crop plan that can 
consist of multiple tasks, and each task can have multiple work orders, and the tasks and work orders track the quantity 
of a product applied to a field, debiting quantities of that product from a central inventory tracking system. 

## Tasks
* has a target quantity of a product (like an herbicide for example) to apply
* each task can have a different product and a different target quantity
* each task has a status such as "not started", "in progress", "completed" that is inherited from the status of the work orders attached to that task

### Worker Orders
* each work order should have a target quantity that is a subset of the task's target product quantity that should be applied with that work order
* each work order should track the actual quantity of the task's target product quantity that was actually applied
* each work order has a status, such as "not started", "in progress", "completed"

## Central Inventory
* the central inventory system should be able to start with a balance of each product 
  (e.g, I have 150 gallons of an herbicide in my warehouse). The supply of a product can only be decreased in this 
  project, until the supply reaches zero
* when each task is completed, a central inventory system is updated to track the amount of each unique product that is actually used.
* if the central inventory system does not have a sufficient supply of the product that a work order specifies, then the app should not allow the work order to be started

## Console App
* The console app should be able to create a plan, add N tasks to the plan, and then assign M work orders for each task.

* In a simple tabular report, list the planned vs actual quantities for each product assigned to each task 
  (formatting not important, the numbers and approach are more important)
  
* The console app should be able to:
    - perform CRUD operations on the plan, tasks and work orders
    - modify product quantities (target and actual) for each task and for each work order
    - show a rolled up actual for each plan at the task and work order level
    - The current balance of any product in the central inventory tracking system should be reported as well.

