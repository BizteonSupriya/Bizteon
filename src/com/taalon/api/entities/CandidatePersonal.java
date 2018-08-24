package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class CandidatePersonal extends SBEntity {
	
	public CandidatePersonal() {
		super();
		
		this.tableName = "candidate_personal";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "candidate_id";
		this.idAuto = false;
		//this.updatedDate = "date_created";
		this.fields = Arrays.asList("candidate_id", "first_name", "last_name", "middle_name", "date_of_birth", "martial_status", "password", "phone_home", "phone_cell", "phone_work", "email1", "email2", "eco_gender", "photo" );
	}
}
