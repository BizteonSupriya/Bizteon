package com.taalon.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class SBResource {
	
	// Below abstract methods have to be implemented by all subclasses
	public abstract String getCreateSQL();
	public abstract String getFindByIdSQL();
	public abstract String getUpdateByIdSQL();
	public abstract String getSearchSQL();
	// Optional methods
	// public String createAll(String sRequestJSON) throws SBBaseException {
	//	throw new SBBaseException("Unhandled method: createAll()");
	// }
	java.sql.Connection conn = null;
	
	protected SBResource() {
		// Get connection
		conn = DBHelper.getConnection();
	}

	public String create(String sRequestJSON) throws SBBaseException  {
		String sResponseJSON = null;
		try {
			String sQuery = getCreateSQL();
			if (sQuery != null) 
				sResponseJSON = insertResource(sQuery, sRequestJSON);
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			throw new SBBaseException("Exception in create() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + sRequestJSON);
		}
		return sResponseJSON;
	}

	public String createAll(String sRequestJSON) throws SBBaseException {
		String sResponseJSON = null;
		try {
			String sQuery = getCreateSQL();
			JSONArray jsonResourceList = new JSONArray(sRequestJSON);
			for (int i = 0; i < jsonResourceList.length(); i++) {
				sResponseJSON = insertResource(sQuery, jsonResourceList.getString(i));
			}
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			throw new SBBaseException("Exception in createAll() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + sRequestJSON);
		}
		return sResponseJSON;
	}

	public String findById(String sRequestJSON) throws SBBaseException {
		String sResponseJSON = null;
		JSONObject jsonRequest = null;
		
		try {
			jsonRequest = new JSONObject(sRequestJSON);

			String sQuery = getFindByIdSQL();
			System.out.println("The search query: " + sQuery);
			sResponseJSON = getResourceById(sQuery, jsonRequest.get("id").toString());
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			throw new SBBaseException("Exception in findById() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + sRequestJSON);
		}
		return sResponseJSON;
	}
	
	public String list(String sOffset, String sRowCount) throws SBBaseException  {
		String sResponseJSON = null;
		try {
			String sQuery = getSearchSQL();
			if ((sOffset != null && !sOffset.trim().equals("")) 
					&& (sRowCount != null && !sRowCount.trim().equals(""))) {
				sQuery  += " LIMIT " + sOffset + ", " + sRowCount;
			} else {
				sQuery  += " LIMIT 100 ";
			}
			
			sResponseJSON = getResourceList(sQuery);
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			throw new SBBaseException("Exception in list() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ ");
		}
		return sResponseJSON;
	}

	public String search(String sRequestJSON, String sOffset, String sRowCount) throws SBBaseException {
		// initialize
		String sResponseJSON = null;

		try {
			JSONObject jsonRequest = null;
			
			// Get the base SQL query
			String sQuery = getSearchSQL();
			
			// Add where clause
			String sWhereClause = "";
			jsonRequest = new JSONObject(sRequestJSON);
			if (jsonRequest.length() > 0 ) {
				JSONArray allKeys = jsonRequest.names();
				int allKeysCount = allKeys.length();
				for (int i = 0; i < allKeysCount; i++) {
					sWhereClause += " AND " + allKeys.getString(i) + "= '" + jsonRequest.get(allKeys.getString(i)) + "' ";
				}
				
				// Check if the query has GROUP BY clause
				int gbIndex = sQuery.toUpperCase().indexOf("GROUP BY");
				if (gbIndex != -1) {
					sQuery = sQuery.substring(0, gbIndex-1) + sWhereClause + sQuery.substring(gbIndex);
					System.out.println("The updated Query: " + sQuery);
				} else {
					sQuery += sWhereClause;
				}
			}
			
			// Check for pagination
			if ((sOffset != null && !sOffset.trim().equals("")) 
					&& (sRowCount != null && !sRowCount.trim().equals(""))) {
				sQuery  += " LIMIT " + sOffset + ", " + sRowCount;
			} else {
				sQuery  += " LIMIT 100 ";
			}
			
			System.out.println("The search query: " + sQuery);

			sResponseJSON = getResourceList(sQuery);
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			ex.printStackTrace();
			System.err.println("Imput: {" + sRequestJSON + ", " + sOffset + ", " + sRowCount + "}");
			throw new SBBaseException("Exception in search() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + sRequestJSON);
		}
		return sResponseJSON;
	}

	public String search(String param, String value, String sOffset, String sRowCount) throws SBBaseException {
		// initialize
		String sResponseJSON = null;

		try {
			// Get the base SQL query
			String sQuery = getSearchSQL();
			
			// Add where clause
			sQuery +=  " AND " + param + " = '" + value + "' ";
			
			// Check for pagination
			if ((sOffset != null && !sOffset.trim().equals("")) 
					&& (sRowCount != null && !sRowCount.trim().equals(""))) {
				sQuery  += " LIMIT " + sOffset + ", " + sRowCount;
			} else {
				sQuery  += " LIMIT 100 ";
			}

			sResponseJSON = getResourceList(sQuery);
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			ex.printStackTrace();
			System.err.println("Imput: {" + param + ": " + value + ", " + sOffset + ", " + sRowCount + "}");
			throw new SBBaseException("Exception in search() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | Imput: " + param + ": " + value);
		}
		return sResponseJSON;
	}
	
	public String updateById(String sRequestJSON) throws SBBaseException  {
		String sResponseJSON = null;

		try {
			String sQuery = getUpdateByIdSQL();

			sResponseJSON = updateResourceById(sQuery, sRequestJSON);
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			throw new SBBaseException("Exception in updateById() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + sRequestJSON);
		}
		return sResponseJSON;
	}

	public String getResourceById(String sQuery, String sRequestID) throws SBBaseException {
		JSONObject jsonResponse = null;
		ResultSetMetaData rsmd = null;
		String sResponseMsg = "";
		int iResponseCode = 1;
		try {
			PreparedStatement ps = conn.prepareStatement(sQuery);
			ps.setString(1, sRequestID);
			ResultSet rs = ps.executeQuery();
			jsonResponse = new JSONObject();
			while (rs.next()) {
				rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					jsonResponse.put(rsmd.getColumnName(i), rs.getString(i));
				}
				sResponseMsg = "Success";
				iResponseCode = 0;
			}
			ps.close();
			rs.close();
		} catch (Exception ex) {
			sResponseMsg = "Error occured while fetching values from DB:" + ex.getMessage();
			iResponseCode = 1;
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
			jsonResponse.put("responseCode", iResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in getResourceById() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}
		return jsonResponse.toString();
	}

	public int getRowCount(String sQuery) throws SBBaseException {
		int iRowCount = 0;
		try {
			sQuery = "SELECT COUNT(0) " + sQuery.substring(sQuery.toUpperCase().indexOf(" FROM"));
			PreparedStatement ps = conn.prepareStatement(sQuery);
			ResultSet rs = ps.executeQuery();
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

	public String getResourceList(String sQuery) throws SBBaseException {
		JSONArray jsonResponseArray = null;
		JSONObject jsonResponse = null;
		ResultSetMetaData rsmd = null;
		String sResponseMsg = "";
		int iResponseCode = 1;
		try {
			jsonResponse = new JSONObject();
			jsonResponseArray = new JSONArray();
			PreparedStatement ps = conn.prepareStatement(sQuery);
			ResultSet rs = ps.executeQuery();
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
			sResponseMsg = "Success";
			iResponseCode = 0;
			ps.close();
			rs.close();
		} catch (Exception ex) {
			sResponseMsg = "Error occured while fetching values from DB:" + ex.getMessage() + " - Query: " + sQuery;
			iResponseCode = 1;
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
			jsonResponse.put("responseCode", iResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in getResourceList() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}

		return jsonResponse.toString();

	}

	public String insertResource(String sQuery, String sRequestJSON) throws SBBaseException {
		JSONObject jsonResponse = null;
		JSONObject jsonRequest = null;
		String sResponseMsg = "";
		int iResponseCode = 1;
		int iReturnID = 0;
		try {
			jsonRequest = new JSONObject(sRequestJSON);
			PreparedStatement ps = conn.prepareStatement(sQuery, Statement.RETURN_GENERATED_KEYS);
			StringTokenizer tokenizer = new StringTokenizer(
					sQuery.substring(sQuery.indexOf('(') + 1, sQuery.indexOf(')')), ",");
			int countTokens = tokenizer.countTokens();
			for (int i = 1; i <= countTokens; i++) {
				String token = tokenizer.nextToken().trim();
				ps.setString(i, jsonRequest.get(token).toString());
			}
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				iReturnID = rs.getInt(1);
				sResponseMsg = "Success";
				iResponseCode = 0;
			} else {
				sResponseMsg = "Failure";
				iResponseCode = 1;
				}
			ps.close();
			rs.close();
		} catch (Exception ex) {
			System.err.println("Error Occured while inserting values : Message from server : " + ex.getMessage());
			sResponseMsg = "Error Occured while inserting values : Message from server : " + ex.getMessage();
			iResponseCode = 1;
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
			jsonResponse.put("responseCode", iResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in insertResource() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}

		return jsonResponse.toString();
	}

	public String updateResourceById(String sQuery, String sRequestJSON) throws SBBaseException {
		JSONObject jsonResponse = null;
		JSONObject jsonRequest = null;
		String sResponseMsg = "";
		int iResponseCode = 1;
		int iReturnID = 0;
		try {
			jsonRequest = new JSONObject(sRequestJSON);
			PreparedStatement ps = conn.prepareStatement(sQuery);
			StringTokenizer tokenizer = new StringTokenizer(
					sQuery.substring(sQuery.indexOf("SET ") + 4, sQuery.indexOf(" WHERE")), ",");
			int countTokens = tokenizer.countTokens();
			for (int i = 0; i < countTokens; i++) {
				String token = tokenizer.nextToken().split("=")[0];
				ps.setString(i + 1, jsonRequest.get(token.trim()).toString());
			}
			// Set the ResourceId field from the Where clause
			ps.setString(countTokens + 1, 
						 jsonRequest.getString(sQuery.substring(sQuery.indexOf("WHERE") + 6, sQuery.lastIndexOf('=')).toString()));
			iReturnID = ps.executeUpdate();
			if (iReturnID > 0) {
				sResponseMsg = "Success";
				iResponseCode = 0;
			} else {
				sResponseMsg = "Failure";
				iResponseCode = 1;
			}
			ps.close();
		} catch (Exception ex) {
			System.err.println("Error Occured while inserting values : Message from server : " + ex.getMessage());
			sResponseMsg = "Error Occured while inserting values : Message from server : " + ex.getMessage();
			iResponseCode = 1;
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
			jsonResponse.put("responseCode", iResponseCode);
		} catch (Exception ex) {
			System.err.println("JSON Object Exception");
			throw new SBBaseException("Exception in updateResourceById() | STACKTRACE ~ " + ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage() + " | QUERY ~ " + sQuery);
		}
		return jsonResponse.toString();
	}
}
