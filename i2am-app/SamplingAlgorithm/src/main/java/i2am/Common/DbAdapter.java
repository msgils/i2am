package i2am.Common;

import java.sql.*;

public class DbAdapter {
    private String host;
    private String databaseName;
    private String userID;
    private String password;
    private Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    private static final String GETTARGETQUERY = "SELECT F_TARGET FROM tbl_intelligent_engine WHERE F_SRC = (SELECT F_SRC FROM tbl_plan WHERE IDX = (SELECT F_PLAN FROM tbl_topology WHERE TOPOLOGY_NAME = ?))";
    private static final String GETTARGETINDEXQUERY = "SELECT COLUMN_INDEX FROM tbl_src_csv_schema WHERE IDX = ?";

    public DbAdapter(){
        host = "jdbc:mariadb://114.70.235.43:3306/";
        databaseName = "i2am";
        userID = "plan-manager";
        password = "dke214";
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(host + databaseName, userID, password);
    }

    public String getTarget(String topologyName) throws SQLException {
        preparedStatement = connection.prepareStatement(GETTARGETQUERY);
        preparedStatement.setString(1, topologyName);
        resultSet = preparedStatement.executeQuery();
        String targetName = resultSet.getString("F_TARGET");
        preparedStatement.close();
        resultSet.close();
        return targetName;
    }

    public int getTargetIndex(String targetName) throws SQLException {
        preparedStatement = connection.prepareStatement(GETTARGETINDEXQUERY);
        preparedStatement.setString(1, targetName);
        resultSet = preparedStatement.executeQuery();
        int targetIndex = resultSet.getInt("COLUMN_INDEX");
        preparedStatement.close();
        resultSet.close();
        return targetIndex-1; // MariDB's target index start from 1
    }
}