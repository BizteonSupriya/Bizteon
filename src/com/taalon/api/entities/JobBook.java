package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class JobBook extends SBEntity {
	
	public JobBook() {
		super();
		
		this.tableName = "job_book";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "jb_id";
		this.idAuto = false;
		//this.updatedDate = "date_created";
		this.fields = Arrays.asList("jb_id", "candidate_id", "qualifiaction_id", "expertise", "experience" );
	}
}
