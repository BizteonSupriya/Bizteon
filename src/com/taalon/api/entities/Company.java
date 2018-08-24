package com.taalon.api.entities;

import java.util.Arrays;

import com.taalon.common.SBEntity;

public class Company extends SBEntity {
	
	public Company() {
		super();
		
		this.tableName = "company";
		//IMPORTANT: Setting Email field as the Unique Id field since there is no other unique id and userid can be null
		this.idField = "comapny_id";
		this.idAuto = false;
		this.updatedDate = "date_created";
		this.fields = Arrays.asList("comapny_id", "billing_contact", "name", "address", "city", "state", "zip", "phone1", "phone2", "url", "key_technologies", "notes","entered_by", "owner", "date_created", "date_modified", "is_hot", "fax_number", "import_id", "default_company" );
	}
}
