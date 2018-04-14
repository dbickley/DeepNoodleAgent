package com.deepnoodle.agent.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.deepnoodle.agent.entity.BaseEntity;

public abstract class BaseDao<T> {
	private static String url = "jdbc:sqlite::memory:";
	protected static ReentrantReadWriteLock dbLock = new ReentrantReadWriteLock(true);
	//private static String url = "jdbc:sqlite:metrics_" + System.currentTimeMillis() + ".db";
	private static Connection conn;

	protected String orderBySql = " order by createtime desc ";
	protected String selectSql;
	protected String insertSql;

	public static Connection getConnection() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection(url);
		}
		return conn;
	}

	protected abstract T buildResults(ResultSet rs, T entity) throws SQLException;

	public static void init() {
		//Create the tables
		List<String> createStatements = new ArrayList<>();
		createStatements.add(ClassDao.createTable);
		createStatements.add(HitDao.createTable);
		createStatements.add(MethodDao.createTable);
		createStatements.add(PackageDao.createTable);
		createStatements.add(StackDao.createTable);
		createStatements.add(ThreadDao.createTable);

		for (String sql : createStatements) {
			dbLock.writeLock().lock();
			try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
				statement.executeUpdate();
			} catch (RuntimeException e) {
				System.out.println("error with sql:" + sql);
				e.printStackTrace();
			} catch (SQLException e) {
				System.out.println("error with sql:" + sql);
				e.printStackTrace();
			} finally {
				dbLock.writeLock().unlock();
			}
		}

	}

	public List<T> select() {
		String sql = getSelectQuery()
				+ getOrderByQuery();
		sql += ";";
		return select(sql);
	}

	public List<T> select(Integer maxRows) {
		String sql = getSelectQuery()
				+ getOrderByQuery();
		sql = addLimit(maxRows, sql);
		sql += ";";
		return select(sql);
	}

	public List<T> select(Integer maxRows, String query) {
		String sql = getSelectQuery()
				+ buildWhere(query)
				+ getOrderByQuery();
		sql = addLimit(maxRows, sql);
		sql += ";";
		return select(sql);
	}

	public T selectOne(String where, SetParametersCommand setParametersCommand) {
		List<T> entities = select(1, where, setParametersCommand);
		if (entities != null && entities.size() > 0) {
			return entities.get(0);
		}
		return null;
	}

	public List<T> select(Integer maxRows, String where, SetParametersCommand setParametersCommand) {
		String sql = getSelectQuery();
		if (where != null) {
			sql += " " + where + " ";
		}
		sql += getOrderByQuery();
		sql = addLimit(maxRows, sql);
		sql += ";";

		return select(sql, setParametersCommand);
	}

	public List<T> select(String sql) {
		SetParametersCommand setParametersCommand = null;
		return select(sql, setParametersCommand);
	}

	public List<T> select(String sql, SetParametersCommand setParametersCommand) {
		List<T> hits = new ArrayList<>();
		dbLock.readLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			if (setParametersCommand != null) {
				setParametersCommand.setParameters(statement);
			}
			ResultSet rs = executeSelect(statement, sql);
			while (rs.next()) {
				T entity = buildResults(rs, null);
				hits.add(entity);
			}
		} catch (RuntimeException e) {
			System.out.println("error with sql:" + sql);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("error with sql:" + sql);
			e.printStackTrace();
		} finally {
			dbLock.readLock().unlock();
		}
		return hits;
	}

	public List<T> selectById(long id, String query) {
		String sql = getSelectQuery()
				+ " where id = ?"
				+ getOrderByQuery();
		sql = addLimit(1, sql);
		sql += ";";

		List<T> entities = new ArrayList<>();
		dbLock.readLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setLong(1, id);
			ResultSet rs = executeSelect(statement, sql);

			// loop through the result set
			while (rs.next()) {
				T entity = buildResults(rs, null);
				entities.add(entity);
			}
		} catch (RuntimeException e) {
			System.out.println("error with sql:" + sql);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("error with sql:" + sql);
			e.printStackTrace();
		} finally {
			dbLock.readLock().unlock();
		}
		return entities;
	}

	protected abstract String getSelectQuery();

	protected abstract String getInsertQuery();

	protected abstract String getOrderByQuery();

	protected void executeInsert(PreparedStatement statement, BaseEntity entity, String sql)
			throws SQLException {
		System.out.println("inserting: " + entity.toString());
		System.out.println("sql: " + sql);
		statement.executeUpdate();
		addPrimaryKeyToObject(entity, statement);
	}

	protected ResultSet executeSelect(PreparedStatement statement, String sql) throws SQLException {
		System.out.println("sql: " + sql);
		return statement.executeQuery();
	}

	protected void executeUpdate(PreparedStatement statement, String sql) throws SQLException {
		System.out.println("sql: " + sql);
		statement.executeUpdate();
	}

	protected void addPrimaryKeyToObject(BaseEntity entity, PreparedStatement statement) throws SQLException {
		ResultSet generatedKeys = statement.getGeneratedKeys();
		if (generatedKeys.next()) {
			entity.setId(generatedKeys.getLong(1));
		}
	}

	//TODO not safe! Plus it needs a lot of work, just a temp piece for testing
	protected String buildWhere(String query) {
		StringBuilder sb = new StringBuilder(" ");
		Map<String, String> queryMap = getQueryMap(query);
		if (query != null && !query.isEmpty()) {
			sb.append(" where ");
			int counter = 0;
			for (Entry<String, String> set : queryMap.entrySet()) {
				sb.append(set.getKey()).append(" = ").append(set.getValue());

				if (counter++ > 0) {
					sb.append(" and ");
				}
			}
		}
		return sb.toString();
	}

	private Map<String, String> getQueryMap(String query) {
		Map<String, String> queryMap = new HashMap<>();
		if (query == null) {
			return queryMap;
		} else {
			try {
				String[] queryTuples = query.split("&");
				for (String queryTuple : queryTuples) {
					String[] q = queryTuple.split("=");
					queryMap.put(q[0], q[1]);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return queryMap;

	}

	protected String addLimit(Integer maxRows, String sql) {
		if (maxRows != null) {
			sql += " limit " + maxRows;
		}
		return sql;
	}

}
