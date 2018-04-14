package com.deepnoodle.agent.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SetParametersCommand {

	void setParameters(PreparedStatement statement) throws SQLException;

}
