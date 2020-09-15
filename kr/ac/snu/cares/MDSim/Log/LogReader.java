package kr.ac.snu.cares.MDSim.Log;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.snu.cares.MDSim.Vo.LogItem;

public class LogReader {
	public static final String MOBILE_MDK_TABLE_NAME = "mklog";
	public static final String MOBILE_MDP_TABLE_NAME = "mplog";
	public static final String WEAR_MDK_TABLE_NAME = "wklog";
	public static final String WEAR_MDP_TABLE_NAME = "wplog";
	public static final String MOBILE_BT_TABLE_NAME = "btlog";
	
	private String dbPath;
	private Connection connection = null;
	
	ResultSet MDLogResultSet;
	ResultSet mobileMDKLogResultSet;
	ResultSet mobileMDPLogResultSet;
	ResultSet wearMDKLogResultSet;
	ResultSet wearMDPLogResultSet;
	
	private LogReader(String dbPath, Connection connection) {
		this.dbPath = dbPath;
		this.connection = connection;
	}
	
	public static LogReader getInstance(String dbPath) {
		if (dbPath == null) return null;
		Connection connection;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:dataset/" + dbPath);	
		} catch(Exception e) { 
			e.printStackTrace();
			return null;
		}
		return	new LogReader(dbPath, connection);
	}
	
	public void init() {
		try {
			Statement statement = connection.createStatement();
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			boolean WEAR_MDK_EXIST = false;
			boolean WEAR_MDP_EXIST = false;
			while (rs.next()) {
				if (rs.getString(3).equals(LogReader.WEAR_MDK_TABLE_NAME))
					WEAR_MDK_EXIST = true;
				if (rs.getString(3).equals(LogReader.WEAR_MDP_TABLE_NAME))
					WEAR_MDP_EXIST = true;
			}
			if ( !((WEAR_MDK_EXIST == true && WEAR_MDP_EXIST == true) || 
						(WEAR_MDK_EXIST == false && WEAR_MDP_EXIST == false)) ){
					System.out.println("Unhandled table organization");
					System.exit(0);
			}
			String query = "SELECT * FROM ( " +
					"SELECT idx, t_datetime, t_log, 1 AS type FROM " + LogReader.MOBILE_MDK_TABLE_NAME +
					" UNION ALL " +
					"SELECT idx, t_datetime, t_log, 2 AS type FROM " + LogReader.MOBILE_MDP_TABLE_NAME;
			if (WEAR_MDK_EXIST == true && WEAR_MDP_EXIST == true) {
				query = query + " UNION ALL " + "SELECT idx, t_datetime, t_log, 3 AS type FROM " + LogReader.WEAR_MDK_TABLE_NAME +
						" UNION ALL " +
						"SELECT idx, t_datetime, t_log, 4 AS type FROM " + LogReader.WEAR_MDP_TABLE_NAME +
						" ) ORDER BY t_datetime";
			}
			else {
				query += " ) ORDER BY t_datetime";
			}
			// if tables does not exist, exception rised.
			try {
				MDLogResultSet = statement.executeQuery(query);
				//mobileMDLogResultSet.next();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			
			try {
				statement = connection.createStatement();
				mobileMDKLogResultSet = statement.executeQuery("select * from " + LogReader.MOBILE_MDK_TABLE_NAME);
				//mobileMDKLogResultSet.next();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				statement = connection.createStatement();
				mobileMDPLogResultSet = statement.executeQuery("select * from " + LogReader.MOBILE_MDP_TABLE_NAME);
				mobileMDPLogResultSet.next();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				statement = connection.createStatement();
				wearMDKLogResultSet = statement.executeQuery("select * from " + LogReader.WEAR_MDK_TABLE_NAME);
				//wearMDKLogResultSet.next();
			} catch (SQLException ex) {
				//ex.printStackTrace();
			}
			try {
				statement = connection.createStatement();
				wearMDPLogResultSet = statement.executeQuery("select * from " + LogReader.WEAR_MDK_TABLE_NAME);
				wearMDPLogResultSet.next();
			} catch (SQLException ex) {
				//ex.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LogItem nextLogItem() {
		LogItem dynamicEventLog = null;
		if (MDLogResultSet == null) return null;
		try {
			MDLogResultSet.next();
			if (MDLogResultSet.isClosed()) return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		LogItem item = LogItemFactory.get(MDLogResultSet, 4);
		return item;
	}
	
	public void test() {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from smartlog");
			while(resultSet.next()) {
				System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/*
	public static void main(String args[]) {

		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(Exception e) { e.printStackTrace(); }
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:dataset/2015-07-25_20_58_18_aggregate.db");
			statement = connection.createStatement();
			
			ResultSet resultSet = statement.executeQuery("select * from smartlog");
			
			while(resultSet.next()) {
				System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
			}
			resultSet.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	*/
}
