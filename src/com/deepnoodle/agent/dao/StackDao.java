package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.deepnoodle.agent.entity.StackEntity;

public class StackDao extends BaseDao<StackEntity> {
	public static StackDao instance = new StackDao();
	public static String createTable = "CREATE TABLE `stack` (" +
			"  `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"  `threadId` INTEGER NOT NULL, " +
			"  `className` text NOT NULL, " +
			"  `methodName` text NOT NULL, " +
			"  `name` text  NULL, " +
			"  `lineNumber` text  NULL, " +
			"  `fileName` text  NULL, " +
			"  `createtime` INTEGER NOT NULL);";

	protected String insertSql = "INSERT INTO stack("
			+ "threadId,"
			+ "className,"
			+ "methodName,"
			+ "name,"
			+ "lineNumber,"
			+ "fileName, "
			+ "createtime) VALUES(?,?,?,?,?,?,?)";

	protected String selectSql = "SELECT "
			+ " id, "
			+ " threadId, "
			+ " className, "
			+ " methodName, "
			+ " name, "
			+ " lineNumber, "
			+ " fileName,"
			+ " createtime "
			+ " FROM stack ";

	private StackDao() {

	}

	@Override
	protected String getSelectQuery() {
		return selectSql;
	}

	@Override
	protected String getInsertQuery() {
		return insertSql;
	}

	@Override
	protected String getOrderByQuery() {
		return orderBySql;
	}

	public void insertStackEntity(StackEntity entity) {
		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(insertSql)) {
			entity.setCreateTime(System.nanoTime());

			statement.setLong(1, entity.getThreadEntityId());
			statement.setString(2, entity.getClassName());
			statement.setString(3, entity.getMethodName());
			statement.setString(4, entity.getName());
			statement.setInt(5, entity.getLineNumber());
			statement.setString(6, entity.getFileName());
			statement.setLong(7, entity.getCreateTime());

			executeInsert(statement, entity, insertSql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
	}

	@Override
	protected StackEntity buildResults(ResultSet rs, StackEntity entity) throws SQLException {
		if (entity == null) {
			entity = new StackEntity();
		}
		entity.setId(rs.getLong("id"));
		entity.setThreadEntityId(rs.getLong("threadId"));
		entity.setClassName(rs.getString("className"));
		entity.setMethodName(rs.getString("methodName"));
		entity.setName(rs.getString("name"));
		entity.setLineNumber(rs.getInt("lineNumber"));
		entity.setFileName(rs.getString("fileName"));
		entity.setCreateTime(rs.getLong("createtime"));
		return entity;

	}
}
