package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Qualification extends SBEntity {
	
	public Qualification() {
		super();
		
		this.tableName = "qualification";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "qualification_id";
		this.idAuto = false;
		//this.updatedDate = "date_created";
		this.fields = Arrays.asList("qualification_id", "highest_qualification" );
	}
}
