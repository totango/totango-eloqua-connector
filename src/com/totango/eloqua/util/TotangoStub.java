package com.totango.eloqua.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.totango.eloqua.data.ActiveList;
import com.totango.eloqua.data.ConfigData;

public class TotangoStub {
	
	private String totangoUrl = "https://app.totango.com/api/v1/accounts/active_list/";
	
	public TotangoStub(){}
		
	/*
	 * Return list of accounts by active list id 
	 */
	public ActiveList queryForActiveList(String activeListId, String token, ConfigData config) throws Exception{
		DefaultHttpClient httpClient = null;		
		List<String> accountsLst = null;
		ActiveList actLst = null;
		String accountId = config.getTotangoAccoutId();
		try{			
			String url = totangoUrl + activeListId + "/current.json?scope=all&offest=0&length=10000";
			httpClient = new DefaultHttpClient();
			HttpGet req = new HttpGet(url);
			req.addHeader("accept", "application/json");
			req.addHeader("Authorization", token);
			
			HttpResponse res = httpClient.execute(req);
			
			if (res.getStatusLine().getStatusCode() != 200) {			
				throw new RuntimeException("Failed : HTTP error code : "
												+ res.getStatusLine().getStatusCode());
			}
							
			BufferedReader br = new BufferedReader(
	                new InputStreamReader((res.getEntity().getContent())));
	
			String output = br.readLine();
			if(output == null){
				throw new RuntimeException("Unable to find the json");
			}
						
			JSONObject mainJson = new JSONObject(output);	
			
			JSONArray jsonArr = mainJson.getJSONArray("accounts");
			accountsLst = new ArrayList<String>(jsonArr.length());
			for(int i=0; i<jsonArr.length(); i++){
				accountsLst.add(jsonArr.getJSONObject(i).getString(accountId));
			}
			
			String name = new JSONObject(mainJson.getString("response_header")).getString("name");
			actLst = new ActiveList(activeListId, name, accountsLst);
					
		}finally{
			if(httpClient!= null)
				httpClient.getConnectionManager().shutdown();
		}
		
		return actLst;
	}
}
