package com.granular;

import com.granular.controller.CommandLineController;
import com.granular.model.Plan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

   public static void main(String[] args) {

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



      CommandLineController clController = new CommandLineController(System.in, System.out);
      clController.start();
   }
}
