package com.taalon.common;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class SBEntity {
	protected String tableName = null;
	protected List<String> fields;
	protected String idField = null;
	protected String requiredParams = null;
	protected String updatedDate = null;
	protected boolean idAuto;
	
	//SQL Query and Parameters
	private String sSQL = null;
	private List<String> params;
	

	// public abstract String getSQLQuery();

	public SBEntity() {
		// init variables
		idField = "ID";
		updatedDate = "UPDATED_DATE";
		idAuto = true;
	}
	
	protected void setInsertSQL() {
		String sSQL2="";
		sSQL = "";
		params = new ArrayList<>();
		
		try {
			for(int i=0; i<fields.size(); i++) {
				// Skip id field
				if (fields.get(i).equals(idField) && idAuto)
					continue;
				else if (fields.get(i).equals(updatedDate)) {
					sSQL += ", " + fields.get(i);
					sSQL2 += ", now()";
				} else {
					sSQL += ", " + fields.get(i);
					sSQL2 += ", ?";
					// Add the field in the required parameter list
					params.add(fields.get(i));
				} 
			}
			sSQL = " INSERT INTO " + tableName + "(" + sSQL.substring(1) + ") VALUES (" + sSQL2.substring(1) + ")";
			System.out.println("SQL is " + sSQL);
		} catch (Exception ex) {
			System.out.println("Method: getInsertSQL - Exeption: " + ex.toString());
		} finally {
			System.out.println("Method: getInsertSQL - SQL query: " + sSQL);
		}

		return;
	}
	
	protected void setUpdateSQL(JSONObject jsonRequest) {
		String sField=null;
		params = new ArrayList<>();
				
		try {
			sSQL = " UPDATE " + tableName + " SET ";
			for(int i=0; i<fields.size(); i++) {
				sField = fields.get(i);
				if (!sField.equals(idField) && jsonRequest.has(sField)) {
					sSQL += sField + " = ?, ";
					// Add the field to list of prepared statement params
					params.add(sField);
				}
			}
			sSQL += updatedDate + " = now() ";
			sSQL += " WHERE " + idField + " = ? ";
			// Add idField as a param
			params.add(idField);
		} catch (Exception ex) {
			System.out.println("Method: getUpdateSQL - Exeption: " + ex.toString());
		} finally {
			System.out.println("Method: getUpdateSQL - SQL query: " + sSQL);
		}
		
		return;
	}
	
	protected void setSelectSQL(JSONObject jsonRequest) {
		String sField=null;
		
		try {
			sSQL = " SELECT " + idField;
			for(int i=0; i<fields.size(); i++)
				sSQL += ", " + fields.get(i);
			sSQL += " FROM " + tableName + " WHERE 1=1 ";
			for(int i=0; i<fields.size(); i++) {
				sField = fields.get(i);
				if (jsonRequest.has(sField)) {
					sSQL += " AND " + sField + " = '" +  jsonRequest.get(sField) + "'";
				}
			}
		} catch (Exception ex) {
			System.out.println("Method: getSelectSQL - Exeption: " + ex.toString());
		} finally {
			System.out.println("Method: getSelectSQL - SQL query: " + sSQL);
		}
		
		return;
	}
	
	public JSONObject insert(JSONObject jsonRequest) throws SBBaseException  {
		CommonDAO dao = null;
		JSONObject jsonResponse = null;
		
		try {
			dao = new CommonDAO();
			setInsertSQL();
			if (sSQL != null) 
				jsonResponse = dao.insertResource(sSQL, params, jsonRequest);
		} catch (Exception ex) {
			System.err.println("Method: create - Exception: " + ex.toString());
			ex.printStackTrace();
			throw new SBBaseException("Exception in create() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonRequest.toString());
		}
		return jsonResponse;
	}

	public JSONObject insertAll(JSONArray jsonResourceList) throws SBBaseException {
		CommonDAO dao = null;
		JSONObject jsonResponse = null;
		try {
			dao = new CommonDAO();

			setInsertSQL();
			for (int i = 0; i < jsonResourceList.length(); i++) {
				jsonResponse = dao.insertResource(sSQL, params, jsonResourceList.getJSONObject(i));
			}
		} catch (Exception ex) {
			System.err.println("Method: insertAll - Exception: " + ex.toString());
			throw new SBBaseException("Exception in insetrtAll() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonResourceList.toString());
		}
		return jsonResponse;
	}
	
	public JSONObject update(JSONObject jsonRequest) throws SBBaseException  {
		JSONObject jsonResponse = null;
		CommonDAO dao = null;

		try {
			dao = new CommonDAO();

			setUpdateSQL(jsonRequest);
			jsonResponse = dao.updateResourceById(sSQL, params, jsonRequest);
		} catch (Exception ex) {
			System.err.println("Method: update - Exception: " + ex.toString());
			ex.printStackTrace();
			throw new SBBaseException("Exception in updateById() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonRequest.toString());
		}
		return jsonResponse;
	}

	public JSONObject updateAll(JSONArray jsonResourceList) throws SBBaseException  {
		JSONObject jsonResponse = null;
		CommonDAO dao = null;

		try {
			dao = new CommonDAO();

			setUpdateSQL(jsonResourceList.getJSONObject(0));

			for (int i = 0; i < jsonResourceList.length(); i++) {
				jsonResponse = dao.updateResourceById(sSQL, params, jsonResourceList.getJSONObject(i));
			}
		} catch (Exception ex) {
			System.err.println("Method: updateAll - Exception: " + ex.toString());
			throw new SBBaseException("Exception in updateById() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonResourceList.toString());
		}
		return jsonResponse;
	}

	public JSONObject findById(JSONObject jsonRequest) throws SBBaseException {
		CommonDAO dao = null;
		JSONObject jsonResponse = null;
		
		try {
			dao = new CommonDAO();

			setSelectSQL(jsonRequest);
			System.out.println("The search query: " + sSQL);
			jsonResponse = dao.getResourceById(sSQL);
		} catch (Exception ex) {
			System.err.println("Method: findById - Exception: " + ex.toString());
			throw new SBBaseException("Exception in findById() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonRequest.toString());
		}
		return jsonResponse;
	}

	public JSONObject findByKey(JSONObject jsonRequest) throws SBBaseException {
		CommonDAO dao = null;
		JSONObject jsonResponse = null;
		
		try {
			dao = new CommonDAO();

			setSelectSQL(jsonRequest);
			System.out.println("The search query: " + sSQL);
			jsonResponse = dao.getResourceById(sSQL);
		} catch (Exception ex) {
			System.err.println("Method: findById - Exception: " + ex.toString());
			throw new SBBaseException("Exception in findById() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonRequest.toString());
		}
		return jsonResponse;
	}
	
	public JSONObject list(JSONObject jsonRequest, String sOffset, String sRowCount) throws SBBaseException  {
		CommonDAO dao = null;
		JSONObject jsonResponse = null;
		
		try {
			setSelectSQL(jsonRequest);
			if ((sOffset != null && !sOffset.trim().equals("")) 
					&& (sRowCount != null && !sRowCount.trim().equals(""))) {
				sSQL  += " LIMIT " + sOffset + ", " + sRowCount;
			} else {
				sSQL  += " LIMIT 100 ";
			}
			
			dao = new CommonDAO();
			jsonResponse = dao.getResourceList(sSQL);
			System.out.println("SBEntity - sResponseJSON: " + jsonResponse.toString());
		} catch (Exception ex) {
			System.err.println("Method: list - Exception: " + ex.toString());
			throw new SBBaseException("Exception in list() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ ");
		}
		
		return jsonResponse;
	}

	public JSONObject search(JSONObject jsonRequest, String sOffset, String sRowCount) throws SBBaseException {
		// initialize
		JSONObject jsonResponse = null;
		CommonDAO dao = null;

		try {
			// Get the base SQL query
			setSelectSQL(jsonRequest);
			
			// Add where clause
			String sWhereClause = "";
			if (jsonRequest.length() > 0 ) {
				JSONArray allKeys = jsonRequest.names();
				int allKeysCount = allKeys.length();
				for (int i = 0; i < allKeysCount; i++) {
					sWhereClause += " AND " + allKeys.getString(i) + "= '" + jsonRequest.get(allKeys.getString(i)) + "' ";
				}
				sSQL += sWhereClause;
			}
			
			// Check for pagination
			if ((sOffset != null && !sOffset.trim().equals("")) 
					&& (sRowCount != null && !sRowCount.trim().equals(""))) {
				sSQL  += " LIMIT " + sOffset + ", " + sRowCount;
			} else {
				sSQL  += " LIMIT 100 ";
			}
			
			System.out.println("The search query: " + sSQL);

			dao = new CommonDAO();
			jsonResponse = dao.getResourceList(sSQL);
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.toString());
			ex.printStackTrace();
			System.err.println("Imput: {" + jsonRequest.toString() + ", " + sOffset + ", " + sRowCount + "}");
			throw new SBBaseException("Exception in search() | STACKTRACE ~ " + ex.toString()  + " | ERROR MESSAGE ~ " + ex.getMessage() + " | REQUEST ~ " + jsonRequest.toString());
		}
		
		return jsonResponse;
	}
}

