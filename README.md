A First Level Header
====================

A Second Level Header
---------------------

Now is the time for all good men to come to
the aid of their country. This is just a
regular paragraph.

The quick brown fox jumped over the lazy
dog's back.

### Header 3

> This is a blockquote.
> 
> This is the second paragraph in the blockquote.
>
> ## This is an H2 in a blockquote
*are emphasized*.
**strong emphasis**
[example link](http://example.com/).
![alt text](/path/to/img.jpg "Title")
 

Totango-Eloqua Connector
========================
Totango-Eloqua is an open source connector that allows you to sync Totango engagement data into Eloqua so you can target marketing campaigns to specific types of user. 

These are called Lifecycle Marketing Campaigns and you can read more about them on the Totango website. Example of such campaigns can target users that have been inactive for a long time, those that have (or have not) used a certain feature of your application, power users and so forth. 

*NOTE:* The Totango-Eloqua Connector uses the following APIs:
1. [Totango REST API](http://www.totango.com/developer/data-api/reference/active_list-api-endpoint/)
2. [Eloqua SOAP API](http://topliners.eloqua.com/groups/eloqua-api/blog/2012/01/23/using-the-eloqua-soap-api-with-e10)


Prerequisites
-------------
Before starting with the Totango-Eloqua connector, you will need:

1. A Totango service up & running. Totango is already collecting engagement data from your web-application. 

2. An Eloqua service up & running. Eloqua lead database populated with your application users and the Totango Insights field added to Eloqua. Read the section �
Setting up Eloqua Totango Insights field� to complete this step. 

3. The totango-eloqua-connector package (this GIT project) downloaded and ready on a machine with a JVM  installed. Read the section �Setting up the totango-eloqua-connector� below to complete this step. 

How it works -- Creating a new campaign 
Follow these steps to create a Lifecycle marketing campaign. The default settings of the eloqua-connector assumes a basic �We Miss You Campaign� targeted at inactive users. Read through this document to configure for your specific campaign needs. 


Step-1: Define an Active-List to capture the behavior you want on Totango. 
(By default, the connector uses the �Inactive Accounts�  to flag all users in accounts that have not used your application for the last two weeks). See Filters & Segments for more information. 

Once you have your Active-List selected, take note of its name and ID (see: http://www.totango.com/developer/data-api/reference/active_list-api/ to find your Active-Lists ID)

Step-2: Configure the connector with the Active-List ID
Configure your Active-List ID in the connector configuration file under config.properties (look under the connector�s installation directory, under the config folder).  Use the totangoActiveLists property to specify active-lists to sync into Eloqua. Provide the list ID and separate by commas if more than one.

For example, if the Active-list on Totango has an ID of 1004, add 1004 to the totangoAclistLists property:

totangoActiveLists=1004



Step3: Run the connector

Use the command below.

This will update the Totango Insight field in Eloqua�s lead database. Users from accounts matching the Active-list will have the name of the list appended into this field. We�ll later use that to setup the campaign in Eloqua.   

NOTE: If you are running an ongoing campaign, you will need to schedule this command to run periodically using cron or your scheduler of choice. You should run the connector 60min before the campaign�s scheduled execution, just to ensure the sync. process has safely completed. 

java com.totango.eloqua.TotangoEloquaConnector
run the connector main class com.totango.eloqua.TotangoEloquaConnector

Output example:
Totango-Eloqua connector is running...
Connected to Eloqua
Reset the insigts field for all Eloqua contacts. This can take a few minutes
Connected to Totango
Updating based on 4 Active-List.This can take a few minutes
Finished successfully. Updated 7,842 contacts in Eloqua, based on 4 Active-Lists in Totango


Step-3: Create or run the Campaign in Eloqua. 
Add to your Eloqua campaign a decision that checks for the �active list name� value inside the �Totango insights� field. In case of true add asset of suitable mail. Repeat this actions for all active lists.

<SCREENSHOT>

Of course, once you have the campaign setup for the first time, you can just re-execute it. 

You�re done! 


Detailed Setup Instructions
See below for details on the one-time setup you need to do on Eloqua-Totango-Connector, and on your Eloqua instance in order to support the soultion

Setting up Eloqua Totango Insights field  (One time setup)
Declare a custom field for contacts called �Totango insights". 

Go to Setup -> Fields & Views and select �Add� (see here http://topliners.eloqua.com/thread/3742). Take note of the field�s Internal name, you will need it to complete the setup as explained in the next section. 

What this does
Once you complete this step, each contact in the Eloqua contacts database will have a Totango Insights field, which will be used by the connector to signal which Active-List it belongs do. 

For example, if you created 3 Active-Lists on Totango: inactive (for inactive accounts), uses-reporting (for accounts that have used the Reporting module recently), Power (for �powerusers�), the Totango Insights field will contain the value �inactive� or �uses-reporting, powerusers� and so forth.  


Setting up the totango-eloquoa-connector
If you didn�t install and setup the environment for the connector yet, follow the instructions under �Installing the connector on your machine�

Change the configuration file config/config.properties under your Totango-Eloqua Connector dir as following:

# Axis path
axisRepoPath=/axis2-1.6.1/client_repo/axis2.xml
# log4j property file path. If doesn't exist it uses a default configuration of INFO level writing to the 
# stdout
log4jConfig=/log4j.properties

# use the totangoActiveLists property to specify active-lists to sync into Eloqua. Provide the list # ID and separate by commas if more than one
totangoActiveLists=45,2030, 4001

# Unique param for Authorization. see at 
# http://www.totango.com/developer/data-api/reference/data-api-authentication/
totangoToken=70c80ab8bf99fa92d0bdb140866064c2cc268b40john@totango.com

# Eloqua params
eloquaUser=John.Smith
eloquaPassword=Example2424
eloquaAccountId=E10AccountExample

# The Eloqua internal name for the �Totango insights� custom field
totangoInsightsField=C_Totango_insights1


# totango-eloqua binding
# The account identity, by default account_id, can replaced with name
totangoAccoutId=account_id
# Eloqua contact field that represents the account id. The connector finds contacts to update 
# according to this field
accountIdField=C_Company


Installing the connector on your machine

Java installation 
To use the Totango-Eloqua application you should install Java environment as following:
1. Download and install Java JRE from http://www.oracle.com/technetwork/java/javase/downloads/index.html . 
2. put your JRE home dir in your system path.

HTTP Client
1. Download the apache http client from http://hc.apache.org/downloads.cgi.
2. Extract the zip/tar file and copy all jar files from the httpcomponents-client-4.1.3\lib dir to the Totango-Eloqua\lib dir. Make sure all jar files are in your java classpath.

Log4J
1. Download Log4j from http://logging.apache.org/log4j/1.2/download.html.
2. Extract the zip/tar file and copy jar to the Totango-Eloqua\lib dir. Make sure the jar file is in your java classpath.
3. Update the log4jConfig property with your log4j configuration file. If not will use default configuration.

Axis2
1. Download the apache axis2 tool from http://axis.apache.org/axis2/java/core/download.cgi , extract it to your file system.
2. Since Eloqua�s api is secured you need to download the rampart module from http://axis.apache.org/axis2/java/rampart/download.html , extract it to your file system install it and copy the rampart.mar file to your axis2 modules dir. Make sure you have set the AXIS2_HOME environment variable.
3. Copy all jar files from your rampart-1.6.1\lib dir to your Totango-Eloqua\lib dir. Make sure all jar files are in your java classpath.
4. Use the apache user guide in http://axis.apache.org/axis2/java/core/docs/userguide.html to create an EloquaStub. Make sure you use the namespace Eloqua and the wsdl from ://secure.eloqua.com/API/1.2/Service.svc?wsdl.
5. Change the EloquaStub.getPolicy method to:
private static org.apache.neethi.Policy getPolicy (java.lang.String policyString) {
  java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(policyString.getBytes());
  try{
    StAXOMBuilder builder = new StAXOMBuilder(bais);
    OMElement documentElement = builder.getDocumentElement();
    return org.apache.neethi.PolicyEngine.getPolicy(documentElement);
  }catch (XMLStreamException e){
    e.printStackTrace();
  }
  return null;
}