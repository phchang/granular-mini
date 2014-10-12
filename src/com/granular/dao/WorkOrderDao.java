package com.granular.dao;

import com.granular.model.WorkOrder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDao extends AbstractBasicDao<WorkOrder> implements Dao<WorkOrder> {

   public WorkOrderDao(Connection conn) {
      super(conn);
   }

   @Override
   public void save(WorkOrder obj) throws SQLException {
      String sql = "INSERT INTO workorder (target_quantity, actual_quantity, status) values (" + obj.getTargetQuantity() + ", " + obj.getActualQuantityApplied() + ", '" + obj.getWorkOrderStatus() + "')";
      executeUpdate(sql);
   }

   @Override
   public void update(WorkOrder obj) throws SQLException {
      String sql = "UPDATE workorder SET (target_quantity, actual_quantity, status) = (" + obj.getTargetQuantity() + ", " + obj.getActualQuantityApplied() + ", " + obj.getWorkOrderStatus() + ")";
      executeUpdate(sql);
   }

   @Override
   public void delete(WorkOrder obj) throws SQLException {
      String sql = "DELETE FROM workorder WHERE workorder_id = " + obj.getId();
      executeUpdate(sql);
   }

   @Override
   public WorkOrder findById(Long id) throws SQLException {
      String sql = "SELECT FROM workorder WHERE workorder_id = " + id;

      final WorkOrder[] order = new WorkOrder[1];

      executeQuery(sql, new ResultSetHandler() {
         @Override
         public void handle(ResultSet rs) throws SQLException {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setId((long) rs.getInt("workorder_id"));
            workOrder.setTargetQuantity(rs.getDouble("target_quantity"));
            workOrder.setActualQuantityApplied(rs.getDouble("actual_quantity"));
            order[0] = workOrder;
         }
      });

      return order[0];
   }

   @Override
   public Iterable<WorkOrder> findAll() throws SQLException {

      final List<WorkOrder> workOrders = new ArrayList<>();

      executeQuery("SELECT workorder_id, target_quantity, actual_quantity, status FROM workorder", new ResultSetHandler() {
         @Override
         public void handle(ResultSet rs) throws SQLException {
            while (rs.next()) {
               WorkOrder order = new WorkOrder();
               order.setId((long) rs.getInt("workorder_id"));
               order.setTargetQuantity(rs.getDouble("target_quantity"));
               order.setActualQuantityApplied(rs.getDouble("actual_quantity"));

               workOrders.add(order);
            }
         }
      });

      return workOrders;
   }
}
