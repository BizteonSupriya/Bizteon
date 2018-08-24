package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Candidate extends SBEntity {
	
	public Candidate() {
		super();
		
		this.tableName = "candidate";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "candidate_id";
		this.idAuto = false;
		this.updatedDate = "date_created";
		this.fields = Arrays.asList("candidate_id", "first_name", "last_name", "middle_name", "date_of_birth", "martial_status", "password", "phone_home", "phone_cell", "phone_work", "address", "city", "state", "zip", "source", "date_available", "can_relocate", "notes", "key_skills","experience", "date_created", "date_modified", "email1", "email2", "is_hot", "work_authorization", "work_authorization_usa", "eco_ethnic_type_id", "eco_veteran_type_id", "eco_disability_status", "eco_gender", "desired_pay", "current_pay", "best_time_to_call", "photo", "industry", "functional_area", "role", "preferred_location", "it_skills", "keyword", "job_category" );
	}
}
