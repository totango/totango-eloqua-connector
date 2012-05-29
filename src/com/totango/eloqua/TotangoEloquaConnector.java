package com.totango.eloqua;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.totango.eloqua.data.ActiveList;
import com.totango.eloqua.data.ConfigData;
import com.totango.eloqua.util.EloquaDispatcher;
import com.totango.eloqua.util.TotangoStub;

//Main entry for Totango Eloqua integration
public class TotangoEloquaConnector {
	
	public static void main(String[] args) {	
		System.out.println("Totango-Eloqua connector is running...");
		ConfigData config = new ConfigData();
		EloquaDispatcher eloqDispatcher = null;
		String[] fieldNamesArr = {"ContactIDExt"};
		
//		Load the configuration
		try {
			config.load();
		} catch (Exception e) {
			System.out.println("ERROR: Unable to load the configuration from .../config/config.properties : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
				
		try {
//			Create the Eloqua SOAP stub
			eloqDispatcher = new EloquaDispatcher(config.getEloquaAccountId(), config.getEloquaUser(), config.getEloquaPassword(), config.getAxisRepoPath());
		} catch (Exception e) {
			System.out.println("ERROR: Unable to create SOAP stub to Eloqua : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
//		Reset the 'insights' field for all Eloqua contacts
		try {			
			List<Integer> eloquaContactLst = eloqDispatcher.queryForContactsIds("ContactIDExt!=-1", fieldNamesArr);
			System.out.println("Find " + eloquaContactLst.size() + " contacts in Eloqua");
			System.out.println("Start reset the " + config.getTotangoInsightsField() + " field for all contacts");			
			
			boolean updateSucceed = eloqDispatcher.updateContacts(eloquaContactLst, config.getTotangoInsightsField(), "");
			if(updateSucceed)
				System.out.println("Succeed to reset the " + config.getTotangoInsightsField() + " field for all contacts");	
			else{
				System.out.println("Unable to reset the " + config.getTotangoInsightsField() + " field for all contacts");
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("Unable to reset the " +  config.getTotangoInsightsField() + " field for all Eloqua contacts");
			e.printStackTrace();
			System.exit(0);
		}
				
		System.out.println("Start query for active lists from Totango");
		TotangoStub tangoStub = new TotangoStub();
		String[] activeListsArr = config.getTotangoActiveLists();		
		Map<String, String> accountsMap = new HashMap<String, String>();
		
		for(int i=0; i<activeListsArr.length; i++){
			try {				
				ActiveList actLst = tangoStub.queryForActiveList(activeListsArr[i], config.getTotangoToken(), config);
				System.out.println(actLst.getAccounts().size() + " accounts were found in active list [name:" + actLst.getName() + ", id:"+ actLst.getId() + "]");
//				Put the active list name as value to it's account for the Eloqua insights field
				for(String account : actLst.getAccounts()){
					if(accountsMap.containsKey(account)){
						String value = accountsMap.get(account) + ", " + actLst.getName();  
						accountsMap.put(account, value);
					}else
						accountsMap.put(account, actLst.getName());
						
				}
			} catch (Exception e) {
				System.out.println("ERROR: Unable to find active list by id " + activeListsArr[i]);
				e.printStackTrace();
				System.exit(0);
			}
		}			
		
				
//		Update Eloqua contacts
		System.out.println("Start updating the Eloqua contacts");
		Set<String> accountsSet = accountsMap.keySet();
		
		List<Integer> contactsLst = null;
		try{
			for (String accId: accountsSet){
				String category = config.getAccountIdField() + "='" + accId + "'";
				contactsLst = eloqDispatcher.queryForContactsIds(category, fieldNamesArr);
				if(contactsLst.size() > 0){
					System.out.println("Find " + contactsLst.size() + " contacts for account '" + accId + "'");
//					Update contacts for the specific account
					eloqDispatcher.updateContacts(contactsLst, config.getTotangoInsightsField(), accountsMap.get(accId));
					System.out.println("Succeed to update " + contactsLst.size() + " contacts for account '" + accId + "'");
				}
			}
		}catch(Exception e){
			System.out.println("ERROR: Unable to update the Eloqua's contacts");
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println("Finished successfully");				
		
	}
	
}
