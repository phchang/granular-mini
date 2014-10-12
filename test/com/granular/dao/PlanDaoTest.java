package com.granular.dao;

import com.granular.model.Plan;
import com.granular.model.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlanDaoTest {

   private Statement st;
   private ResultSet rs;
   private Connection conn;

   @Before
   public void setUp() throws Exception {
      conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
   }

   @After
   public void tearDown() throws Exception {
      rs.close();
      st.close();
      conn.close();
   }

   @Test
   public void testPlanDao_SaveAndRetrieve() throws SQLException {

      Task task1 = new Task();
      task1.setTargetQuantity(1d);

      Task task2 = new Task();
      task2.setTargetQuantity(2d);

      Plan plan1 = new Plan();
      plan1.setTasks(Arrays.asList(task1, task2));

      PlanDao dao = new PlanDao(conn);
      dao.save(plan1);

      Iterable<Plan> plans = dao.findAll();

      int size = 0;

      List<Plan> results = new ArrayList<>();

      for (Plan plan : plans) {
         results.add(plan);
         size++;
      }

      assertEquals(1, size);
      assertEquals(2, results.get(0).getTasks().size());
      assertEquals(Double.valueOf(1d), results.get(0).getTasks().get(0).getTargetQuantity());
      assertEquals(Double.valueOf(2d), results.get(0).getTasks().get(1).getTargetQuantity());
   }
}
