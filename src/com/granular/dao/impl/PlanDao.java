package com.granular.dao.impl;

import com.granular.dao.AbstractBasicDao;
import com.granular.dao.Dao;
import com.granular.model.Plan;

public class PlanDao extends AbstractBasicDao<Plan> implements Dao<Plan> {

   public void remove(Long id) {

      Plan planToRemove = null;

      for (Plan plan : objects) {
         if (plan.getId().equals(id)) {
            planToRemove = plan;
         }
      }

      objects.remove(planToRemove);
   }
}