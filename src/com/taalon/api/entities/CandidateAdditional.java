package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class CandidateAdditional extends SBEntity {
	
	public CandidateAdditional() {
		super();
		
		this.tableName = "candidate_additional";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "candidate_id";
		this.idAuto = false;
		this.updatedDate = "date_created";
		this.fields = Arrays.asList("candidate_id", "address", "city", "state", "zip", "source", "date_available", "can_relocate", "notes", "key_skills", "current_employer", "experience", "date_created", "date_modified", "is_hot", "work_authorization", "work_authorization_usa", "eco_ethnic_type_id", "eco_veteran_type_id", "eco_disability_status", "desired_pay", "current_pay", "best_time_to_call", "industry", "functional_area", "role", "preferred_location", "it_skills", "keyword", "job_category" );
	}
}
