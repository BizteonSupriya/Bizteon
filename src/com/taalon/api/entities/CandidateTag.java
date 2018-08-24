package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class CandidateTag extends SBEntity {
	
	public CandidateTag() {
		super();
		
		this.tableName = "candidate_tag";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "candidate_id";
		this.idAuto = false;
		//this.updatedDate = "date_created";
		this.fields = Arrays.asList("id","candidate_id", "qualification_id", "course", "specialization", "university/college", "course_type", "passing_year", "skills" );
	}
}
