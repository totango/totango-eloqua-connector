package com.totango.eloqua;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.totango.eloqua.data.ActiveList;
import com.totango.eloqua.data.ConfigData;
import com.totango.eloqua.util.EloquaDispatcher;
import com.totango.eloqua.util.TotangoStub;

//Main entry for Totango Eloqua integration
public class TotangoEloquaConnector {
	
	private static Logger logger = Logger.getLogger(TotangoEloquaConnector.class);
	
	public static void main(String[] args) {				
		EloquaDispatcher eloqDispatcher = null;
		String[] fieldNamesArr = {"ContactIDExt"};
		
//		Load the configuration
		ConfigData config = loadConfiguration();
		setupLogger(config.getLog4jConfig());
		
		logger.info("Totango-Eloqua connector is running...");		
		try {
//			Create the Eloqua SOAP stub
			eloqDispatcher = new EloquaDispatcher(config.getEloquaAccountId(), config.getEloquaUser(), config.getEloquaPassword(), config.getAxisRepoPath());
			logger.info("Connected to Eloqua");
		} catch (Exception e) {
			logger.error("Unable to create SOAP stub to Eloqua", e);			
			System.exit(0);
		}
		
//		Reset the 'insights' field for all Eloqua contacts
		try {			
			List<Integer> eloquaContactLst = eloqDispatcher.queryForContactsIds("ContactIDExt!=-1", fieldNamesArr);
			logger.debug("Find " + eloquaContactLst.size() + " contacts in Eloqua");
			logger.info("Reset the " + config.getTotangoInsightsField() + " field for all Eloqua contacts. This can take a few minutes...");						
			
			boolean updateSucceed = eloqDispatcher.updateContacts(eloquaContactLst, config.getTotangoInsightsField(), "");
			if(updateSucceed)
				logger.debug("Succeed to reset the " + config.getTotangoInsightsField() + " field for all contacts");	
			else{
				logger.error("Unable to reset the " + config.getTotangoInsightsField() + " field for all contacts");
				System.exit(0);
			}
		} catch (Exception e) {
			logger.error("Unable to reset the " +  config.getTotangoInsightsField() + " field for all Eloqua contacts");			
			System.exit(0);
		}
				
//		Start query for active lists from Totango
		TotangoStub tangoStub = new TotangoStub();		
		String[] activeListsArr = config.getTotangoActiveLists();		
		Map<String, String> accountsMap = new HashMap<String, String>();
		logger.info("Connected to Totango");
		for(int i=0; i<activeListsArr.length; i++){
			try {				
				ActiveList actLst = tangoStub.queryForActiveList(activeListsArr[i], config.getTotangoToken(), config);
				logger.debug(actLst.getAccounts().size() + " accounts were found in active list [name:" + actLst.getName() + ", id:"+ actLst.getId() + "]");
				
//				Put the active list name as value to it's account for the Eloqua insights field
				for(String account : actLst.getAccounts()){
					if(accountsMap.containsKey(account)){
						String value = accountsMap.get(account) + ", " + actLst.getName();  
						accountsMap.put(account, value);
					}else
						accountsMap.put(account, actLst.getName());
						
				}
			} catch (Exception e) {
				logger.error("Unable to find active list by id " + activeListsArr[i], e);				
				System.exit(0);
			}
		}			
		
				
//		Update Eloqua contacts
		logger.info("Updating Eloqua based on " + activeListsArr.length + " Active-Lists. This can take a few minutes...");
		int numberOfContacts = 0;
		Set<String> accountsSet = accountsMap.keySet();		
		List<Integer> contactsLst = null;
		try{
			for (String accId: accountsSet){
				String category = config.getAccountIdField() + "='" + accId + "'";
				contactsLst = eloqDispatcher.queryForContactsIds(category, fieldNamesArr);
				numberOfContacts += contactsLst.size(); 
				if(contactsLst.size() > 0){
					logger.debug("Find " + contactsLst.size() + " contacts for account '" + accId + "'");					
//					Update contacts for the specific account
					eloqDispatcher.updateContacts(contactsLst, config.getTotangoInsightsField(), accountsMap.get(accId));
					logger.debug("Succeed to update " + contactsLst.size() + " contacts for account '" + accId + "'");					
				}
			}
		}catch(Exception e){
			logger.error("Unable to update the Eloqua's contacts");			
			System.exit(0);
		}
		
		System.out.println("Finished successfully. Updated " + numberOfContacts + " contacts on Eloqua, based in " + activeListsArr.length + " Active-Lists in Totango");				
		
	}
	
	
	
	public static ConfigData loadConfiguration(){
		ConfigData config = new ConfigData();
		try {
			config.load();			
		} catch (Exception e) {
			System.out.println("ERROR: Unable to load the configuration from .../config/config.properties : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
		return config;
	}
	
	
	public static void setupLogger(String path){	
		if(path == null || path.equals("")){
			Properties configFile = new Properties();
			configFile.put("log4j.rootLogger", "info, stdout");
			configFile.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
			configFile.put("log4j.appender.stdout.Target", "System.out");
			configFile.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
			PropertyConfigurator.configure(configFile);
		} else
			PropertyConfigurator.configure(path);
		
	}
}
