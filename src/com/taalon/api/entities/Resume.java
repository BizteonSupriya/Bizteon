package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Resume extends SBEntity {
	
	public Resume() {
		super();
		
		this.tableName = "resume";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "resume_id";
		this.idAuto = false;
		//this.updatedDate = "date_created";
		this.fields = Arrays.asList("resume_id", "candidate_id", "qualification", "resume" );
	}
}
