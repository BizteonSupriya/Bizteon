package com.taalon.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class CommonDAO {
	java.sql.Connection conn = null;
	public static String SB_SUCCESS = "Success";
	public static String SB_FAILURE = "Failure";

	public CommonDAO() {
		// Get connection
		conn = DBHelper.getConnection();
	}

	public int getRowCount(String sQuery) throws SBBaseException {
		int iRowCount = 0;
		try {
			sQuery = "SELECT COUNT(0) " + sQuery.substring(sQuery.toUpperCase().indexOf(" FROM"));
			Statement ps = conn.createStatement();
			ResultSet rs = ps.executeQuery(sQuery);
			if (rs.next()) {
				iRowCount = rs.getInt(1);
			}
			ps.close();
			rs.close();
		} catch (Exception ex) {
			System.err.println("Error occured while fetching values from DB:" + ex.getMessage());
			throw new SBBaseException("Exception in getRowCount() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				// Ignore any exception
			}
		}
		return iRowCount;
	}

	public JSONObject getResourceById(String sQuery) throws SBBaseException {
		JSONObject jsonResponse = null;
		ResultSetMetaData rsmd = null;
		String sResponseMsg = "";
		String sResponseCode = "1";
		try {
			jsonResponse = new JSONObject();

			Statement ps = conn.createStatement();
			ResultSet rs = ps.executeQuery(sQuery);
			if (rs.next()) {
				System.out.println("getResourceById(): Record found");
				rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					jsonResponse.put(rsmd.getColumnName(i), rs.getString(i));
				}
				sResponseMsg = SB_SUCCESS;
				sResponseCode = "0";
			} else {
				System.out.println("getResourceById(): Record not found");
				sResponseMsg = SB_FAILURE;
				sResponseCode = "1";
			}
			ps.close();
			rs.close();
		} catch (Exception ex) {
			sResponseMsg = "Error occured while fetching values from DB:" + ex.getMessage();
			sResponseCode = "1";
			System.err.println("Error occured while fetching values from DB:" + ex.getMessage());
			throw new SBBaseException("Exception in getResourceById() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				// Ignore any exception
			}
		}
		
		try {
			jsonResponse.put("responseMessage", sResponseMsg);
			jsonResponse.put("responseCode", sResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in getResourceById() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}
		
		return jsonResponse;
	}

	public JSONObject getResourceList(String sQuery) throws SBBaseException {
		JSONArray jsonResponseArray = null;
		JSONObject jsonResponse = null;
		ResultSetMetaData rsmd = null;
		String sResponseMsg = "";
		String sResponseCode = "1";
		try {
			jsonResponse = new JSONObject();
			jsonResponseArray = new JSONArray();
			Statement ps = conn.createStatement();
			ResultSet rs = ps.executeQuery(sQuery);
			while (rs.next()) {
				rsmd = rs.getMetaData();
				JSONObject jsonTemp = new JSONObject();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					jsonTemp.put(rsmd.getColumnName(i), rs.getString(i));
				}
				jsonResponseArray.put(jsonTemp);
			}
			if(sQuery.indexOf("LIMIT") > 0) {
				String sRowCountQuery = sQuery.substring(0,(sQuery.indexOf("LIMIT") - 1));
				int rowCount = getRowCount(sRowCountQuery);
				jsonResponse.put("TOTAL_ROWS", rowCount);
			}
			jsonResponse.put("RESULTS_SET", jsonResponseArray);
			sResponseMsg = SB_SUCCESS;
			sResponseCode = "0";
			ps.close();
			rs.close();
		} catch (Exception ex) {
			sResponseMsg = "Error occured while fetching values from DB:" + ex.getMessage() + " - Query: " + sQuery;
			sResponseCode = "1";
			System.err.println(sResponseMsg);
			ex.printStackTrace();
			throw new SBBaseException("Exception in getResourceList() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				// Ignore any exception
			}
		}
		
		try {
			jsonResponse.put("responseMessage", sResponseMsg);
			jsonResponse.put("responseCode", sResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in getResourceList() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}

		System.out.println("CommonDAO - jsonResponse: " + jsonResponse);
		return jsonResponse;

	}

	public JSONObject insertResource(String sQuery, List<String> params, JSONObject jsonRequest) throws SBBaseException {
		JSONObject jsonResponse = null;
		String sResponseMsg = "SB_SUCCESS";
		String sResponseCode = "0";
		int iReturnID = -1;

		try {
			PreparedStatement ps = conn.prepareStatement(sQuery, Statement.RETURN_GENERATED_KEYS);
			//Replaced this login with a list of parameters as input. YK 08072018
			// StringTokenizer tokenizer = new StringTokenizer(
			//		sQuery.substring(sQuery.indexOf('(') + 1, sQuery.indexOf(')')), ",");
			//int countTokens = tokenizer.countTokens();
			for (int i = 0; i < params.size(); i++) {
				String token = params.get(i).trim();
				
				if (jsonRequest.has(token)) 
					ps.setString(i+1, jsonRequest.get(token).toString());
				else 
					ps.setString(i, "");
			}
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				iReturnID = rs.getInt(1);
			}
			ps.close();
			rs.close();
		} catch (Exception ex) {
			System.err.println("Error Occured while inserting values : Message from server : " + ex.getMessage());
			ex.printStackTrace();
			sResponseMsg = "Error Occured while inserting values : Message from server : " + ex.getMessage();
			sResponseCode = "1";
			throw new SBBaseException("Exception in insertResource() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				// Ignore any exception
			}
		}

		try {
			jsonResponse = new JSONObject();
			jsonResponse.put("responseId", iReturnID);
			jsonResponse.put("responseMessage", sResponseMsg);
			jsonResponse.put("responseCode", sResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in insertResource() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}

		return jsonResponse;
	}

	public JSONObject updateResourceById(String sQuery, List<String> params, JSONObject jsonRequest) throws SBBaseException {
		JSONObject jsonResponse = null;
		String sResponseMsg = "";
		String sResponseCode = "1";
		int iReturnID = 0;

		try {
			PreparedStatement ps = conn.prepareStatement(sQuery);
			// Replaced this login with a list of parameters as input. YK 08072018
			// StringTokenizer tokenizer = new StringTokenizer(
			// 		sQuery.substring(sQuery.indexOf("SET ") + 4, sQuery.indexOf(" WHERE")), ",");
			// int countTokens = tokenizer.countTokens();
			for (int i = 0; i < params.size(); i++) {
				ps.setString(i + 1, jsonRequest.get(params.get(i).trim()).toString());
			}
			// Not required anymore
			// Set the ResourceId field from the Where clause
			// ps.setString(params.size(), jsonRequest.getString(params);
			iReturnID = ps.executeUpdate();
			if (iReturnID > 0) {
				sResponseMsg = SB_SUCCESS;
				sResponseCode = "0";
			} else {
				sResponseMsg = SB_FAILURE;
				sResponseCode = "1";
			}
			ps.close();
		} catch (Exception ex) {
			System.err.println("Error Occured while inserting values : Message from server : " + ex.getMessage());
			ex.printStackTrace();
			sResponseMsg = "Error Occured while inserting values : Message from server : " + ex.getMessage();
			sResponseCode = "1";
			throw new SBBaseException("Exception in updateResourceById() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		} finally {
			try {
				conn.close();
			} catch (Exception ex) {
				// Ignore any exception
			}
		}

		try {
			jsonResponse = new JSONObject();
			jsonResponse.put("responseId", iReturnID);
			jsonResponse.put("responseMessage", sResponseMsg);
			jsonResponse.put("responseCode", sResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in updateResourceById() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}
		return jsonResponse;
	}

}

