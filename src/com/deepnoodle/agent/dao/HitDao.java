package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.deepnoodle.agent.entity.HitEntity;

public class HitDao extends BaseDao<HitEntity> {
	public static HitDao instance = new HitDao();
	public static String createTable = "CREATE TABLE `hit` (" +
			"  `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"  `methodId` INTEGER NOT NULL, " +
			"  `callingHitId` INTEGER NULL, " +
			"  `threadId` INTEGER NOT NULL, " +
			"  `args` text  NULL, " +
			"  `returned` text  NULL, " +
			"  `duration` INTEGER  NULL, " +
			"  `createtime` INTEGER  NOT NULL, " +
			"  `endtime` INTEGER  NULL "
			+ ");";

	static String insertSql = "INSERT INTO hit("
			+ "methodId,"
			+ "threadId,"
			+ "callingHitId,"
			+ "args,"
			+ "returned,"
			+ "duration,"
			+ "createtime, "
			+ "endtime "
			+ ") values(?,?,?,?,?,?,?,?) ;";

	static String selectSql = "SELECT "
			+ "id, "
			+ "methodId,"
			+ "threadId,"
			+ "callingHitId,"
			+ "args,"
			+ "returned,"
			+ "duration,"
			+ "createtime, "
			+ "endtime "
			+ "FROM hit ";

	private HitDao() {

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

	public HitEntity insertHit(HitEntity entity) {

		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(insertSql)) {
			entity.setCreateTime(System.nanoTime());

			statement.setLong(1, entity.getMethodId());
			statement.setLong(2, entity.getThreadId());
			if (entity.getCallingHitId() != null) {
				statement.setLong(3, entity.getCallingHitId());
			}
			statement.setString(4, entity.getArgs());
			statement.setString(5, entity.getReturned());
			statement.setLong(6, entity.getDuration());
			statement.setLong(7, entity.getCreateTime());
			if (entity.getEndTime() != null) {
				statement.setLong(8, entity.getEndTime());
			}

			executeInsert(statement, entity, insertSql);

			return entity;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
		return null;
	}

	public HitEntity updateHit(HitEntity entity) {
		String sql = "update hit "
				+ "set "
				+ "returned=?,"
				+ "duration=?, "
				+ "endtime=?"
				+ " where id=?";

		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, entity.getReturned());
			statement.setLong(2, entity.getDuration());
			statement.setLong(3, entity.getEndTime());
			statement.setLong(4, entity.getId());

			executeUpdate(statement, sql);

			return entity;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbLock.writeLock().unlock();
		}
		return entity;
	}
	//
	//	public List<HitEntity> select(Integer maxRows) {
	//		String sql = selectSql + orderBySql;
	//		sql = addLimit(maxRows, sql);
	//		sql += ";";
	//
	//		List<HitEntity> hits = new ArrayList<>();
	//		dbLock.readLock().lock();
	//		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
	//			ResultSet rs = executeSelect(statement, sql);
	//
	//			// loop through the result set
	//			while (rs.next()) {
	//				HitEntity entity = buildResults(rs, null);
	//				hits.add(entity);
	//			}
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		} finally {
	//			dbLock.readLock().unlock();
	//		}
	//		return hits;
	//	}

	@Override
	protected HitEntity buildResults(ResultSet rs, HitEntity entity) throws SQLException {
		if (entity == null) {
			entity = new HitEntity();
		}
		entity.setId(rs.getLong("id"));
		entity.setMethodId(rs.getLong("methodId"));
		entity.setCallingHitId(rs.getLong("callingHitId"));
		entity.setThreadId(rs.getLong("threadId"));
		entity.setArgs(rs.getString("args"));
		entity.setReturned(rs.getString("returned"));
		entity.setDuration(rs.getLong("duration"));
		entity.setCreateTime(rs.getLong("createtime"));
		entity.setEndTime(rs.getLong("endtime"));
		return entity;
	}

	//	public  List<TopHit> selectLongestHits(Integer maxRows) {
	//		String sql = "SELECT methodName, avg(duration) as duration, count(*) as count FROM hit group by methodName order by avg(duration) desc";
	//		if (maxRows != null) {
	//			sql += " limit " + maxRows;
	//		}
	//		List<TopHit> tophits = new ArrayList<>();
	//		dblock.lock();
	//		try (Statement statement = getConnection().createStatement();
	//				ResultSet rs = statement.executeQuery(sql)) {
	//			int i = 0;
	//			// loop through the result set
	//			while (rs.next()) {
	//				TopHit hit = new TopHit(
	//						rs.getString("methodName"),
	//						rs.getLong("duration"),
	//						rs.getLong("count"),
	//						i++);
	//				tophits.add(hit);
	//			}
	//		} catch (SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} finally {
	//			dblock.unlock();
	//		}
	//		return tophits;
	//	}
	//
	//	public  List<TopHit> selectMostHits(Integer maxRows) {
	//		String sql = "SELECT methodName, avg(duration) as duration, count(*) as count FROM hit group by methodName order by count(*) desc";
	//		if (maxRows != null) {
	//			sql += " limit " + maxRows;
	//		}
	//		List<TopHit> tophits = new ArrayList<>();
	//		dblock.lock();
	//		try (Statement statement = getConnection().createStatement();
	//				ResultSet rs = statement.executeQuery(sql)) {
	//			int i = 0;
	//			// loop through the result set
	//			while (rs.next()) {
	//				TopHit hit = new TopHit(
	//						rs.getString("methodName"),
	//						rs.getLong("duration"),
	//						rs.getLong("count"),
	//						i++);
	//				tophits.add(hit);
	//			}
	//		} catch (SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} finally {
	//			dblock.unlock();
	//		}
	//		return tophits;
	//	}

}
