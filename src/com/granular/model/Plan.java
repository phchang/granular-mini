package com.granular.model;

import java.util.ArrayList;
import java.util.List;

public class Plan {
   private Long id;
   private String name;
   private List<Task> tasks;

   public Plan() {
      this.tasks = new ArrayList<>();
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<Task> getTasks() {
      return tasks;
   }

   public void setTasks(List<Task> tasks) {
      this.tasks = tasks;
   }

   public void addTask(Task task) {
      // by default, not started
      task.setTaskStatus(Status.NOT_STARTED);
      this.tasks.add(task);
   }
}
