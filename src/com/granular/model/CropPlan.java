package com.granular.model;

import java.util.List;

public class CropPlan {
   private List<Task> tasks;

   public CropPlan() {}

   public List<Task> getTasks() {
      return tasks;
   }

   public void setTasks(List<Task> tasks) {
      this.tasks = tasks;
   }
}
