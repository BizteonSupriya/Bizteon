package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Job extends SBEntity {
	
	public Job() {
		super();
		
		this.tableName = "job";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "job_id";
		this.idAuto = false;
		//this.updatedDate = "date_created";
		this.fields = Arrays.asList("job_id", "job_title", "company_id", "posted_on", "is_active", "location", "descrption", "experience", "qualification", "contact_id" );
	}
}
