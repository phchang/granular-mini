package com.granular.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ResultSetHandler<T> {
   void handle(ResultSet rs) throws SQLException;
}
