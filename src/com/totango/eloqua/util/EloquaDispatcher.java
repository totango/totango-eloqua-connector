package com.totango.eloqua.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;

import com.eloqua.secure.api._1_2.ArrayOfDynamicEntity;
import com.eloqua.secure.api._1_2.DynamicEntity;
import com.eloqua.secure.api._1_2.DynamicEntityFields;
import com.eloqua.secure.api._1_2.EloquaStub;
import com.eloqua.secure.api._1_2.EntityFields_type0;
import com.eloqua.secure.api._1_2.EntityType;
import com.eloqua.secure.api._1_2.Query;
import com.eloqua.secure.api._1_2.QueryResponse;
import com.eloqua.secure.api._1_2.Update;
import com.eloqua.secure.api._1_2.UpdateResponse;
import com.eloqua.secure.api._1_2.UpdateResult;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


public class EloquaDispatcher {

	private EloquaStub stub;
	
//	Create stub for Eloqua
	public EloquaDispatcher(String accId, String user, String password, String axis2RepoPath) throws Exception{		
		ConfigurationContext ctx =  
				ConfigurationContextFactory.createConfigurationContextFromFileSystem(axis2RepoPath);
		stub = new EloquaStub(ctx, "https://secure.eloqua.com/API/1.2/Service.svc");		
		ServiceClient srvClient = stub._getServiceClient();		
		Options options = srvClient.getOptions();
		srvClient.engageModule("rampart");			
		options.setUserName(accId +"\\"+user);  
		options.setPassword(password);		
	}
	
		
	/*
	 *  queryCategory example -> "C_Company=Apple"
	 *  fieldNames - if null gets all fields
	 */
	public List<Integer> queryForContactsIds(String queryCategory, String[] fieldNames) throws Exception{
		Query query = new Query();
		EntityType type = new EntityType();
		type.setID(0);
		type.setName("Contact");
		type.setType("Base");
		query.setEloquaType(type);
		if(fieldNames != null){
			ArrayOfstring arrStr = new ArrayOfstring();
			arrStr.setString(fieldNames);
			query.setFieldNames(arrStr);
		}				
		query.setSearchQuery(queryCategory);
		query.setPageSize(200);
		
				
		QueryResponse res = null;
		List<Integer> idLst = new LinkedList<Integer>();
		int pageNumber = 0;
		
//		Eloqua allows to get no more than 200 rows per page
		while(true){
			query.setPageNumber(++pageNumber);
			res = stub.query(query);
			stub.cleanup();
			
			if(res != null){				
				if(res.getQueryResult().getEntities().getDynamicEntity() == null){
					System.out.println("Unable to find Eloqua's contacts for the category " + queryCategory);
					return idLst;
				}
//				Add contacts to the list
				for(int i=0; i < res.getQueryResult().getEntities().getDynamicEntity().length; i++){
					
					idLst.add(res.getQueryResult().getEntities().getDynamicEntity()[i].getId());
				}	
				
				if (res.getQueryResult().getEntities().getDynamicEntity().length < 200){
					return idLst;
				}					
			}else{
				throw new RuntimeException("Unable to find contacts in Eloqua");
			}				
		}
				
	}
	
	
	/*
	 *  contactLst - List of contact's ids to update
	 *  
	 */
	public boolean updateContacts(List<Integer> contactLst, String fieldName, String fieldValue) throws Exception{	
		boolean result = true;
		
//		Create the field
		EntityFields_type0 entityField = new EntityFields_type0();
		entityField.setInternalName(fieldName);
		entityField.setValue(fieldValue);
		EntityFields_type0[] entityFieldArr = new EntityFields_type0[1];		
		entityFieldArr[0] = entityField;		
		DynamicEntityFields entityFields = new DynamicEntityFields();		
		entityFields.setEntityFields(entityFieldArr);
		
//		Declare the type of the field - Contact
		EntityType type = new EntityType();
		type.setID(0);
		type.setName("Contact");
		type.setType("Base");
		
		
//		update 200 contacts each iteration
		for(int i=0; i<contactLst.size(); i++){
			int entityArrLength = contactLst.size()-i>200 ? 200 : contactLst.size()-i;			
			DynamicEntity[] dynaEntityArr = new DynamicEntity[entityArrLength];
			
			for(int j=0; j<dynaEntityArr.length; j++,i++){
				DynamicEntity entity = new DynamicEntity();				
				entity.setFieldValueCollection(entityFields);
				entity.setEntityType(type);
				entity.setId(contactLst.get(i));
				dynaEntityArr[j] = entity;
			}
			
			ArrayOfDynamicEntity arrEntity = new ArrayOfDynamicEntity();
			arrEntity.setDynamicEntity(dynaEntityArr);
			Update update = new Update();		
			update.setEntities(arrEntity);		
			UpdateResponse upRes = stub.update(update);
			UpdateResult[] resultArr = upRes.getUpdateResult().getUpdateResult();
			
//			Check if succeed
			for(int index=0; index<resultArr.length; index++){
				if(!resultArr[index].getSuccess()){
					System.out.println("Unable to update contact " + resultArr[index].getID() + " : " + resultArr[index].getErrors().getError()[0].getMessage());											
					result = false;
				}					
			}			
		}

		stub.cleanup();
		return result;		
	}		

}
