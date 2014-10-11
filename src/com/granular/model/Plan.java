package com.granular.model;

import java.util.List;

public class Plan {
   private List<Task> tasks;

   public Plan() {}

   public List<Task> getTasks() {
      return tasks;
   }

   public void setTasks(List<Task> tasks) {
      this.tasks = tasks;
   }
}
