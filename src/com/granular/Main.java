package com.granular;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

   public static void main(String[] args) {

        /*
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

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("test input");

      try {
         String name = reader.readLine();
         System.out.println("input = " + name);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
