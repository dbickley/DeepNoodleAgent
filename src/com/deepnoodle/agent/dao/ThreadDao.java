package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.deepnoodle.agent.entity.ThreadEntity;

public class ThreadDao extends BaseDao<ThreadEntity> {
	public static ThreadDao instance = new ThreadDao();
	public static String createTable = "CREATE TABLE `thread` (" +
			"  `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"  `threadId` INTEGER NOT NULL, " +
			"  `name` text NOT NULL, " +
			"  `state` text NOT NULL, " +
			"  `className` text  NOT NULL, " +
			"  `createtime` INTEGER NOT NULL);";

	protected String insertSql = "INSERT INTO thread("
			+ "threadId, "
			+ "name, "
			+ "state, "
			+ "className, "
			+ "createtime"
			+ ") VALUES(?,?,?,?,?)";

	protected String selectSql = "SELECT id,"
			+ "threadId,"
			+ "name,"
			+ "state,"
			+ "classname,"
			+ "createtime "
			+ "FROM thread";

	private ThreadDao() {

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

	public ThreadEntity insert(ThreadEntity entity) {

		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(insertSql)) {
			entity.setCreateTime(System.nanoTime());

			statement.setLong(1, entity.getThreadId());
			statement.setString(2, entity.getName());
			statement.setString(3, entity.getState());
			statement.setString(4, entity.getClassName());
			statement.setLong(5, entity.getCreateTime());

			executeInsert(statement, entity, insertSql);
		} catch (SQLException e) {
			System.out.println("sql");
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
		return entity;
	}

	public ThreadEntity findByThreadId(Long threadId) {
		SetParametersCommand setParametersCommand = new SetParametersCommand() {

			@Override
			public void setParameters(PreparedStatement statement) throws SQLException {
				statement.setLong(1, threadId);
			}
		};
		return selectOne(" where threadId = ? ", setParametersCommand);
	}

	@Override
	public ThreadEntity buildResults(ResultSet rs, ThreadEntity threadEntity) throws SQLException {
		if (threadEntity == null) {
			threadEntity = new ThreadEntity();
		}
		threadEntity.setId(rs.getLong("id"));
		threadEntity.setId(rs.getLong("threadId"));
		threadEntity.setName(rs.getString("name"));
		threadEntity.setState(rs.getString("state"));
		threadEntity.setClassName(rs.getString("classname"));
		threadEntity.setCreateTime(rs.getLong("createtime"));
		return threadEntity;
	}

	public void updateThreadState(Long id, String state) {
		String sql = "UPDATE thread SET state = ?  "
				+ "WHERE id = ?";

		dbLock.writeLock().lock();
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
			pstmt.setString(1, state);
			pstmt.setLong(2, id);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
	}

}
