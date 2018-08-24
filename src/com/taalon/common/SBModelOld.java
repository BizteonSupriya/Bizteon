package com.taalon.common;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.*;

import com.taalon.shared.*;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

//import javax.servlet.ServletException;
//import org.json.JSONArray;
//import org.json.JSONObject;

//import com.cgsits.shared.Notes;
//import com.cgsits.shared.Task;

public abstract class SBModelOld {/*
	// Global variables
	protected String actionType = "";
	protected String modelName = "";
	protected String orgId = "";
	protected String userId = "";
	protected String orgList = "";
	protected String roleList = "";
	protected boolean isAdmin = false;
	protected JSONObject jsonRequest = null;

	protected abstract String performSubAction(HttpServletRequest request, boolean hasNotes) throws ServletException;
	
	 * private void authorize(HttpServletRequest request) throws
	 * ServletException { try { userId = "1"; roleList = "[admin]"; isAdmin =
	 * false; orgId = "2"; orgList = "[1,2]"; jsonRequest = new
	 * JSONObject(request.getParameter("fyiRequest")); if (isAdmin) {
	 * jsonRequest.put("org_id", orgId); } jsonRequest.put("member_id", userId);
	 * // Get action. actionType = request.getParameter("action");
	 * 
	 * // ToDo - Check if the user is authorized to perform this action for the
	 * model and org. } catch (Exception ex) { System.err.println(
	 * "Method - authorize() - Exception: "); ex.printStackTrace(); throw new
	 * ServletException("Exception in authorize() | STACKTRACE ~ " +
	 * ex.toString() + " | ERROR MESSAGE ~ " + ex.getMessage()); } }
	 

	private void authorize(HttpServletRequest request) throws ServletException {
		try {
			// Check for first access after login
			if (request.getSession().getAttribute("UserId") == null) {
				System.out.println("Authorized and initialized request received.");
				// Get the login id
				String userName = request.getUserPrincipal().getName();

				// Get userId and roleList
				Users users = new Users();
				JSONArray jsonUserList = (new JSONObject(users.search("u.username", userName, "", "")))
						.getJSONArray("RESULTS_SET");
				userId = jsonUserList.getJSONObject(0).getString("id");
				for (int i = 0; i < jsonUserList.length(); i++) {
					roleList += "[" + jsonUserList.getJSONObject(i).getString("rolename") + "]";
					// To function as an admin or not will be decided by the
					// particular Model. Yogesh 7/18/16
					// if
					// (jsonUserList.getJSONObject(i).getString("rolename").equals("admin"))
					// isAdmin = true;
				}

				// Set session variable
				request.getSession().setAttribute("UserId", userId);
				request.getSession().setAttribute("RoleList", roleList);

				if (isAdmin) {
					// Get Org List
					// OrgAdmin orgAdmin = new OrgAdmin();
					// String sResultSet = orgAdmin.list("admin_id", userId, "", "");
					// System.out.println(sResultSet);
					// JSONArray jsonList = (new JSONObject(sResultSet)).getJSONArray("RESULTS_SET");
					// for (int i = 0; i < jsonList.length(); i++) {
					//	orgList += "[" + jsonList.getJSONObject(i).getString("org_id") + "]";
					// }
					// orgId = jsonList.getJSONObject(0).getString("org_id");

					// Set more session variable
					request.getSession().setAttribute("OrgId", orgId);
					request.getSession().setAttribute("OrgList", orgList);
				}
				System.out.println("Authorized and initialized. UserId: " + userId + "; OrgId: " + orgId);
			} else {
				System.out.println("Authorized request received.");
				userId = request.getSession().getAttribute("UserId").toString();
				roleList = request.getSession().getAttribute("RoleList").toString();
				if (isAdmin) {
					if (request.getSession().getAttribute("OrgId") == null) {
						// Get Org List
						// OrgAdmin orgAdmin = new OrgAdmin();
						// String sResultSet = orgAdmin.search("admin_id", userId, "", "");
						// System.out.println(sResultSet);
						// JSONArray jsonList = (new JSONObject(sResultSet)).getJSONArray("RESULTS_SET");
						// for (int i = 0; i < jsonList.length(); i++) {
						// 	orgList += "[" + jsonList.getJSONObject(i).getString("org_id") + "]";
						// }
						// orgId = jsonList.getJSONObject(0).getString("org_id");

						// Set more session variable
						request.getSession().setAttribute("OrgId", orgId);
						request.getSession().setAttribute("OrgList", orgList);
					} else {
						orgId = request.getSession().getAttribute("OrgId").toString();
						orgList = request.getSession().getAttribute("OrgList").toString();
					}
				}
				System.out.println("Authorized. UserId: " + userId + "; OrgId: " + orgId);
			}

			System.out.println("fyiRequest is: " + request.getParameter("fyiRequest"));
			jsonRequest = new JSONObject(request.getParameter("fyiRequest").replaceAll("\n", " --linebreak-- "));
			if (isAdmin) {
				// Add the current selected org_id as a Request parameter
				jsonRequest.put("org_id", orgId);
			} else {
				jsonRequest.put("member_id", userId);
			}

			// Get action.
			actionType = request.getParameter("action");

			// ToDo - Check if the user is authorized to perform this action for
			// the model and org.
		} catch (Exception ex) {
			System.err.println("Method - authorize() - Exception: " + ex.toString());
			ex.printStackTrace();
			throw new ServletException("Exception in authorize() | STACKTRACE ~ " + ex.toString()
					+ " | ERROR MESSAGE ~ " + ex.getMessage());
		}
	}

	private void authenticate(HttpServletRequest request) throws ServletException {
		try {
			// Check if user has logged in
			if (request.getUserPrincipal() == null) {
				System.out.println("Method - authenticate() - Failed: " + request.getRemoteUser() + " - "
						+ request.getAuthType() + " - " + request.isSecure());
				throw new ServletException("Exception in authenticate() | ERROR MESSAGE ~ User not authenticated.");
			} else {
				System.out.println("Method - authenticate() - Successful: " + request.getRemoteUser() + " - "
						+ request.getUserPrincipal().getName() + " - " + request.getAuthType() + " - "
						+ request.isSecure());
			}
		} catch (Exception ex) {
			System.err.println("Method - authenticate() - Exception: " + ex.toString());
			ex.printStackTrace();
			throw new ServletException("Exception in authenticate() | ERROR MESSAGE ~ " + ex.getMessage());
		}
	}

	public void setAdmin(boolean isAdminVal) {
		isAdmin = isAdminVal;
	}

	public String performAction(HttpServletRequest request, boolean hasNotes) throws ServletException {
		System.out.println("SBModel.performAction() - SessionID: " + request.getSession().getId());
		authenticate(request);
		authorize(request);

		return performSubAction(request, hasNotes);
	}

	
	 * // Abstract method that has to implemented by child classes public
	 * abstract void initResource(); // returns JSONString public abstract
	 * String performOtherAction(HttpServletRequest request, HttpServletResponse
	 * response);
	 * 
	 * // protected variables protected SBResource resource = null; protected
	 * Notes notes = null;
	 * 
	 * public String getDetailsById(String sReqourceId, String sResponse) {
	 * return sResponse; }
	 * 
	 * public HttpServletResponse performAction(HttpServletRequest request,
	 * HttpServletResponse response, boolean hasNotes) throws ServletException {
	 * String sResponse = null; Notes notes = null; JSONObject jsonNotes = null;
	 * JSONObject jsonResponse = null;
	 * 
	 * try { // Initialize the resource initResource();
	 * 
	 * String actionType = request.getParameter("action");
	 * 
	 * switch (actionType) { case "onload": System.out.println("json"
	 * +request.getParameter("onloadRequest"));
	 * 
	 * /* JSONObject jsonRequest = null; CommonDAO dao = new CommonDAO(); String
	 * sResponseJSON = null; jsonResponse = new JSONObject(); String sResourceId
	 * = request.getParameter("resourceId"); jsonRequest = new
	 * JSONObject(request.getParameter("onloadRequest")); if
	 * (jsonRequest.length() > 0 ) { JSONArray allKeys = jsonRequest.names();
	 * int allKeysCount = allKeys.length(); for (int i = 0; i <allKeysCount;
	 * i++) { String sQuery =
	 * "SELECT OPTION_NAME,OPTION_VALUE FROM SB_LOOKUP_MAP WHERE LOOKUP_KEY='" +
	 * allKeys.getString(i) + "' and RESOURCE_ID='" + sResourceId + "'";
	 * sResponseJSON = dao.getResourceList(sQuery);
	 * jsonResponse.put(allKeys.getString(i), sResponseJSON); } }
	 *
	 * 
	 * JSONObject searchJSON = new JSONObject(); searchJSON = new
	 * JSONObject(request.getParameter("onloadRequest")); JSONObject
	 * searchResponseJSON = new JSONObject(); if(searchJSON.length() > 0) {
	 * JSONArray allKeys = searchJSON.names(); int allKeysCount =
	 * allKeys.length(); for (int i = 0; i < allKeysCount; i++) { resource = new
	 * Task(); JSONObject tempJSON = new JSONObject();
	 * tempJSON.put(allKeys.getString(i), searchJSON.get(allKeys.getString(i)));
	 * System.out.println(tempJSON); sResponse =
	 * resource.search(tempJSON.toString(),request.getParameter("rowOffset"),
	 * request.getParameter("rowCount")); System.out.println(sResponse);
	 * tempJSON = new JSONObject(sResponse);
	 * searchResponseJSON.put(allKeys.getString(i) + "_TOTAL_ROWS"
	 * ,tempJSON.getJSONArray("RESULTS_SET").length()); } }
	 * searchResponseJSON.put("responseCode", 0);
	 * //response.getWriter().println(jsonResponse);
	 * System.out.println(searchResponseJSON);
	 * response.getWriter().println(searchResponseJSON.toString()); break;
	 * 
	 * case "create": System.out.println(request.getParameter("createRequest"));
	 * sResponse = resource.create(request.getParameter("createRequest"));
	 * System.out.println(sResponse); // If has notes - create all notes if
	 * (hasNotes) { jsonResponse = new JSONObject(sResponse); notes = new
	 * Notes(); JSONArray notesListJSON = new
	 * JSONArray(request.getParameter("notesList")); for (int i = 0; i <
	 * notesListJSON.length(); i++) { jsonNotes = new
	 * JSONObject(notesListJSON.getString(i)); jsonNotes.put("Task_Id",
	 * jsonResponse.get("responseId")); jsonNotes.put("Parent_Id",
	 * jsonResponse.get("responseId")); notes.create(jsonNotes.toString()); } }
	 * response.getWriter().println(sResponse); break;
	 * 
	 * case "list": sResponse = resource.list(request.getParameter("rowOffset"),
	 * request.getParameter("rowCount"));
	 * response.getWriter().println(sResponse.toString()); break;
	 * 
	 * case "search": sResponse =
	 * resource.search(request.getParameter("searchRequest"),request.
	 * getParameter("rowOffset"), request.getParameter("rowCount"));
	 * 
	 * response.getWriter().println(sResponse); break;
	 * 
	 * case "update": sResponse =
	 * resource.updateById(request.getParameter("updateRequest")); // If has
	 * notes if (hasNotes) { notes = new Notes(); JSONArray jsonNotesList = new
	 * JSONArray(request.getParameter("notesList")); for (int i = 0; i <
	 * jsonNotesList.length(); i++) { jsonNotes = new
	 * JSONObject(jsonNotesList.getString(i));
	 * notes.create(jsonNotes.toString()); } }
	 * response.getWriter().println(sResponse); break;
	 * 
	 * case "findById": sResponse =
	 * resource.findById(request.getParameter("requestId")); // If Details are
	 * required. this method will get those sResponse =
	 * getDetailsById(request.getParameter("requestId"), sResponse);
	 * response.getWriter().println(sResponse); break;
	 * 
	 * default: sResponse = performOtherAction(request,response);
	 * response.getWriter().println(sResponse); }
	 * 
	 * // performPostAction() } catch (Exception e) { System.err.println(
	 * "Error occured in SBModel.performAction(): " + e.getMessage()); throw new
	 * ServletException("Exception in SBModel.performAction() | STACKTRACE ~ " +
	 * e.toString()); } return response; }
	 
*/}
