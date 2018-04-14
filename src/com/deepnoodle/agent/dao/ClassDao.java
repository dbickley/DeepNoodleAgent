package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.deepnoodle.agent.entity.ClassEntity;

public class ClassDao extends BaseDao<ClassEntity> {

	public static ClassDao instance = new ClassDao();
	public static String createTable = "CREATE TABLE `class` (" +
			"  `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"  `packageId` INTEGER NOT NULL, " +
			"  `name` text NOT NULL, " +
			"  `createtime` INTEGER  NOT NULL"
			+ ");";

	static String insertSql = "INSERT INTO class("
			+ "packageId,"
			+ "name,"
			+ "createTime)"
			+ " values(?,?,?) "
			+ ";";

	static String selectSql = "SELECT "
			+ "id, "
			+ "packageId,"
			+ "name,"
			+ "createtime "
			+ "FROM class ";

	private ClassDao() {

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

	public ClassEntity insert(ClassEntity entity) {

		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(insertSql)) {
			entity.setCreateTime(System.nanoTime());

			statement.setLong(1, entity.getPackageId());
			statement.setString(2, entity.getName());
			statement.setLong(3, entity.getCreateTime());

			executeInsert(statement, entity, insertSql);

			return entity;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
		return null;
	}

	public ClassEntity findByNameAndPackageId(String className, Long packageId) {
		SetParametersCommand setParametersCommand = new SetParametersCommand() {

			@Override
			public void setParameters(PreparedStatement statement) throws SQLException {
				statement.setString(1, className);
				statement.setLong(2, packageId);
			}
		};
		return selectOne(" where name=? and packageId=? ", setParametersCommand);
	}

	@Override
	protected ClassEntity buildResults(ResultSet rs, ClassEntity entity) throws SQLException {
		if (entity == null) {
			entity = new ClassEntity();
		}
		entity.setId(rs.getLong("id"));
		entity.setPackageId(rs.getLong("packageId"));
		entity.setName(rs.getString("name"));
		entity.setCreateTime(rs.getLong("createtime"));

		return entity;
	}

}
