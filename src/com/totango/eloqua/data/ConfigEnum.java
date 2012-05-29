package com.totango.eloqua.data;

public enum ConfigEnum {	
	AXIS_REPO_PATH ("axisRepoPath"),
	
//	Totango params		
	TOTANGO_ACTIVE_LISTS ("totangoActiveLists"),	
	TOTANGO_TOKEN ("totangoToken"),
	TOTANGO_ACCOUNT_ID ("totangoAccoutId"),
		
//	Eloqua params
	ELOQUA_URL ("eloquaUrl"),
	ELOQUA_USER ("eloquaUser"),
	ELOQUA_PASSWORD ("eloquaPassword"),
	ELOQUA_ACCOUNT_ID ("eloquaAccountId"),
	TOTANGO_INSIGHTS_FIELD ("totangoInsightsField"),
	ACCOUNT_ID_FIELD ("accountIdField");
	
	private String conf;
	
	ConfigEnum(String conf){
		this.conf = conf;
	}
	
	public String value(){
		return conf;
	}
}
