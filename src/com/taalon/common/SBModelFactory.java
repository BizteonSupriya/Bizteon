package com.taalon.common;

import com.taalon.api.models.*;

public class SBModelFactory {
	public static SBViewModel getModel(String modelName){
		SBViewModel sbModel = null;
		switch (modelName) {
			case "user":
				//sbModel = new AccountModel();
				break;
				
			case "notify":
				//sbModel = new NotifyModel();
				break;
			
			default:
		}
		
		System.out.println("Model name: '" + modelName + "' - SB Model: " + sbModel);
		return sbModel;
	}
}
