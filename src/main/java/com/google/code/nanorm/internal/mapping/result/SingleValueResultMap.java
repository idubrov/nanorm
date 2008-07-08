package com.google.code.nanorm.internal.mapping.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.internal.Request;

public class SingleValueResultMap implements ResultMap {

	public void processResultSet(Request request, ResultSet rs,
			ResultCallback<Object> callback) throws SQLException {
		
		// TODO: Check we have single column!
		
		callback.handleResult(rs.getObject(1));
	}

}
