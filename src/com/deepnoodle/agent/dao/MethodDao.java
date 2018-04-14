package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.deepnoodle.agent.entity.MethodEntity;

public class MethodDao extends BaseDao<MethodEntity> {

	public static MethodDao instance = new MethodDao();
	public static String createTable = "CREATE TABLE `method` (" +
			"  `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"  `classId` INTEGER NOT NULL, " +
			"  `name` text  NOT NULL, " +
			"  `modifiers` text  NULL, " +
			"  `isMethod` boolean  true, " +
			"  `annotations` text  NULL, " +
			"  `createtime` INTEGER  NOT NULL"
			+ ");";

	static String insertSql = "INSERT INTO method("
			+ "classId, "
			+ "name, "
			+ "modifiers, "
			+ "isMethod, "
			+ "annotations, "
			+ "createtime "
			+ ") "
			+ " values(?,?,?,?,?,?) ;";

	static String selectSql = "SELECT "
			+ "id, "
			+ "classId,"
			+ "name,"
			+ "modifiers,"
			+ "isMethod,"
			+ "annotations,"
			+ "createtime "
			+ "FROM method ";

	private MethodDao() {

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

	public MethodEntity insert(MethodEntity entity) {

		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(insertSql)) {
			entity.setCreateTime(System.nanoTime());

			statement.setLong(1, entity.getClassId());
			statement.setString(2, entity.getName());
			statement.setString(3, entity.getModifiers());
			statement.setBoolean(4, entity.isMethod());
			statement.setString(5, entity.getAnnotations());
			statement.setLong(6, entity.getCreateTime());

			executeInsert(statement, entity, insertSql);

			return entity;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
		return null;
	}

	public MethodEntity findByMethodNameAndClassId(String methodName, long classId) {
		String sql = selectSql;
		sql += " where name=? and classId=? ";
		sql += orderBySql;
		sql = addLimit(1, sql);
		sql += ";";

		MethodEntity methodEntity = null;
		dbLock.readLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, methodName);
			statement.setLong(2, classId);

			ResultSet rs = executeSelect(statement, sql);

			while (rs.next()) {
				methodEntity = buildResults(rs, methodEntity);
				return methodEntity;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbLock.readLock().unlock();
		}
		return methodEntity;
	}

	@Override
	protected MethodEntity buildResults(ResultSet rs, MethodEntity entity) throws SQLException {
		if (entity == null) {
			entity = new MethodEntity();
		}
		entity.setId(rs.getLong("id"));
		entity.setClassId(rs.getLong("classId"));
		entity.setName(rs.getString("name"));
		entity.setModifiers(rs.getString("modifiers"));
		entity.setMethod(rs.getBoolean("isMethod"));
		entity.setAnnotations(rs.getString("annotations"));
		entity.setCreateTime(rs.getLong("createtime"));

		return entity;
	}

}
