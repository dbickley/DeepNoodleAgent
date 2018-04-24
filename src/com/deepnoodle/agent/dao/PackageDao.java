package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.deepnoodle.agent.entity.PackageEntity;

public class PackageDao extends BaseDao<PackageEntity> {
	public static PackageDao instance = new PackageDao();

	public static String createTable = "CREATE TABLE `package` ( " +
			"  `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"  `parentPackageId` INTEGER NULL, " +
			"  `name` text, " +
			"  `createtime` INTEGER  NOT NULL "
			+ ");"
			+ "CREATE unique INDEX parentPackageId_name_idx ON package (parentPackageId,name);";

	protected String insertSql = "INSERT INTO package("
			+ " parentPackageId, "
			+ " name, "
			+ " createtime "
			+ " ) values(?,?,?) ; ";

	protected String selectSql = "SELECT "
			+ " id, "
			+ " parentPackageId, "
			+ " name, "
			+ " createtime "
			+ " FROM package ";

	private PackageDao() {

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

	public PackageEntity insert(PackageEntity entity) {

		dbLock.writeLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(insertSql)) {
			entity.setCreateTime(System.nanoTime());
			if (entity.getParentPackageId() != null) {
				statement.setLong(1, entity.getParentPackageId());
			}
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

	public PackageEntity findByNameAndParent(String packageName, Long parentPackageId) {

		SetParametersCommand setParametersCommand = new SetParametersCommand() {

			@Override
			public void setParameters(PreparedStatement statement) throws SQLException {
				statement.setString(1, packageName);
				if (parentPackageId != null) {
					statement.setLong(2, parentPackageId);
				}
			}
		};
		String where;
		if (parentPackageId != null) {
			where = " where name=? and parentPackageId=? ";
		} else {
			where = " where name=? and parentPackageId is null ";
		}
		return selectOne(where, setParametersCommand);

	}

	@Override
	public List<PackageEntity> select(Integer maxRows, String query) {
		String sql = selectSql
				+ buildWhere(query)
				+ orderBySql;
		sql = addLimit(maxRows, sql);
		sql += ";";

		List<PackageEntity> entities = new ArrayList<>();
		dbLock.readLock().lock();
		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			ResultSet rs = executeSelect(statement, sql);

			// loop through the result set
			while (rs.next()) {
				PackageEntity entity = buildResults(rs, null);
				entities.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbLock.readLock().unlock();
		}
		return entities;
	}

	@Override
	protected PackageEntity buildResults(ResultSet rs, PackageEntity entity) throws SQLException {
		if (entity == null) {
			entity = new PackageEntity();
		}
		entity.setId(rs.getLong("id"));
		entity.setName(rs.getString("name"));
		entity.setParentPackageId(rs.getLong("parentPackageId"));
		entity.setCreateTime(rs.getLong("createtime"));
		return entity;
	}

}
