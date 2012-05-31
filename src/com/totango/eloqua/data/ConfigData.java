package com.totango.eloqua.data;

import java.io.FileInputStream;
import java.util.Properties;


public class ConfigData {
	
	private String axisRepoPath;
	private String log4jConfig;
	

	//	Totango params
	private String[] totangoActiveLists;	
	private String totangoToken;
	private String totangoAccoutId;
	
	
	//	Eloqua params	
	private String eloquaUser;
	private String eloquaPassword;
	private String eloquaAccountId;
	private String totangoInsightsField;	
	private String accountIdField;
	
	
	public void load() throws Exception{
		Properties configFile = new Properties();
		try {
		    configFile.load(new FileInputStream("config/config.properties"));		    
		} catch (Exception e) {			
			throw new RuntimeException("Unable to load the configuration", e);
		}
		
		this.setAxisRepoPath(configFile.getProperty(ConfigEnum.AXIS_REPO_PATH.value()));
		this.setLog4jConfig(configFile.getProperty(ConfigEnum.LOG4J_CONFIG.value()));
				
		this.setTotangoToken(configFile.getProperty(ConfigEnum.TOTANGO_TOKEN.value()));		
		this.setTotangoActiveLists(configFile.getProperty(ConfigEnum.TOTANGO_ACTIVE_LISTS.value()).trim().split(","));
		this.setTotangoAccoutId(configFile.getProperty(ConfigEnum.TOTANGO_ACCOUNT_ID.value()));
		
		this.setEloquaUser(configFile.getProperty(ConfigEnum.ELOQUA_USER.value()));
		this.setEloquaPassword(configFile.getProperty(ConfigEnum.ELOQUA_PASSWORD.value()));
		this.setEloquaAccountId(configFile.getProperty(ConfigEnum.ELOQUA_ACCOUNT_ID.value()));
		this.setTotangoInsightsField(configFile.getProperty(ConfigEnum.TOTANGO_INSIGHTS_FIELD.value()));
		this.setAccountIdField(configFile.getProperty(ConfigEnum.ACCOUNT_ID_FIELD.value()));
	}
	
//	Getters & Setters
	public String[] getTotangoActiveLists() {
		return totangoActiveLists;
	}

	public void setTotangoActiveLists(String[] totangoActiveLists) {
		this.totangoActiveLists = totangoActiveLists;
	}
	
	public String getAxisRepoPath() {
		return axisRepoPath;
	}
	
	public void setAxisRepoPath(String axisRepoPath) {
		this.axisRepoPath = axisRepoPath;
	}
			
	public String getTotangoToken() {
		return totangoToken;
	}
	
	public void setTotangoToken(String totangoToken) {
		this.totangoToken = totangoToken;
	}
			
	public String getEloquaUser() {
		return eloquaUser;
	}
	
	public void setEloquaUser(String eloquaUser) {
		this.eloquaUser = eloquaUser;
	}
	
	public String getEloquaPassword() {
		return eloquaPassword;
	}
	
	public void setEloquaPassword(String eloquaPassword) {
		this.eloquaPassword = eloquaPassword;
	}
	
	public String getEloquaAccountId() {
		return eloquaAccountId;
	}
	
	public void setEloquaAccountId(String eloquaAccountId) {
		this.eloquaAccountId = eloquaAccountId;
	}
	
	public String getTotangoInsightsField() {
		return totangoInsightsField;
	}

	public void setTotangoInsightsField(String totangoInsightsField) {
		this.totangoInsightsField = totangoInsightsField;
	}

	public String getAccountIdField() {
		return accountIdField;
	}

	public void setAccountIdField(String accountIdField) {
		this.accountIdField = accountIdField;
	}

	public String getTotangoAccoutId() {
		return totangoAccoutId;
	}

	public void setTotangoAccoutId(String totangoAccoutId) {
		this.totangoAccoutId = totangoAccoutId;
	}

	public String getLog4jConfig() {
		return log4jConfig;
	}

	public void setLog4jConfig(String log4jConfig) {
		this.log4jConfig = log4jConfig;
	}
}
