package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Application extends SBEntity {
	
	public Application() {
		super();
		
		this.tableName = "app_tbl";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "joborder_id";
		this.idAuto = false;
		this.updatedDate = "date_created";
		this.fields = Arrays.asList("joborder_id", "recruiter", "contact_id", "company_id", "entered_by", "owner", "client_job_id", "title", "description", "notes", "type", "duration","rate_max", "salary", "status", "is_hot", "openings", "city", "state", "start_date", "date_created", "date_modified", "company_department_id", "is_admin_hidden", "openings_available", "questionaire_id"  );
	}
}
