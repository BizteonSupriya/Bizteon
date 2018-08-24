package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Contact extends SBEntity {
	
	public Contact() {
		super();
		
		this.tableName = "contact";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "company_id";
		this.idAuto = false;
		this.updatedDate = "date_created";
		this.fields = Arrays.asList("contact_id", "company_id", "first_name", "last_name", "title", "email1", "email2", "phone_work", "phone_cell", "phone_other","address", "city", "state", "zip", "notes", "date_created", "date_modified"  );
	}
}
