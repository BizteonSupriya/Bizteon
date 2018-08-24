package com.taalon.common;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.tomcat.util.codec.binary.Base64;

public class DBHelper {
	public DBHelper() {
		// Do nothing
	}
	
	public static Connection getConnection() {
		Connection conn = null;
		try 
		{ 
			Class.forName("com.mysql.jdbc.Driver");
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			String sURL = (String) envCtx.lookup("DBConnectURL");
			String sDBUser = (String) envCtx.lookup("DBUserName");
			byte[] decodedBytesArray  = Base64.decodeBase64( (String) envCtx.lookup("DBPassword"));
			String decodedDBPassword = new String(decodedBytesArray);
			System.out.println("DBHelper.getConnection(): url - " + sURL + "; User: - " + sDBUser + "; Password - " + decodedDBPassword);
			/*
			String url = "jdbc:mysql://cgs-devdb.czspzuv6lcaw.us-west-2.rds.amazonaws.com:3306/Customer 360" ;   //System.getProperty("JDBC_CONNECTION_STRING") + "/" + System.getProperty("DBNAME"); 
			byte[] decodedBytesArray  = Base64.decodeBase64("Y3lwcmU1NWdz");
			*/
			/*
			String url = "jdbc:mysql://184.173.24.28:3306/Customer 360" ;   //System.getProperty("JDBC_CONNECTION_STRING") + "/" + System.getProperty("DBNAME"); 
			String decodedDBPassword = new String("");
			*/
			/*
			conn = DriverManager.getConnection(url,"cgsadmin",decodedDBPassword);
			*/
			conn = DriverManager.getConnection(sURL,sDBUser,decodedDBPassword);
	    } catch(Exception e) {
			System.err.println("Error Occured while creating connection : Message from server : " + e.getMessage());
		}
		
		return conn;
	}
}
