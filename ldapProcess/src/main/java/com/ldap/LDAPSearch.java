package com.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

public class LDAPSearch
{
	
	private LDAPConnection getConnection(String loginDN,String password,String serverHost,int port) throws Exception {
        LDAPConnection lc = new LDAPConnection();
        lc.connect(serverHost, port );
        lc.bind( LDAPConnection.LDAP_V3, loginDN,  password.getBytes("UTF8"));
        return lc;
	}
	
	@SuppressWarnings("rawtypes")
	private LDAPSearchResults searchFromLDAP(LDAPConnection connection, String searchBase, int searchScope, String searchFilter) throws LDAPException {
		
		LDAPSearchResults searchResults = connection.search(searchBase, searchScope, searchFilter, null, false );
        
        while (searchResults.hasMore() ) {
        	
            LDAPEntry nextEntry = null;
            try {
                nextEntry = searchResults.next();
            } catch(LDAPException e ) {
                System.out.println("Error: " + e.toString());
                continue;
            }
            
            LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
            
            Iterator allAttributes = attributeSet.iterator();
            
            while ( allAttributes.hasNext() ) {
            	
                LDAPAttribute attribute = (LDAPAttribute)allAttributes.next();
                String attributeName = attribute.getName();
                Enumeration allValues = attribute.getStringValues();
                
                if (allValues != null) {
                    while ( allValues.hasMoreElements() ) {
                        String value = (String)allValues.nextElement();
                        System.out.println(attributeName + ":  " + value );
                    }
                }
            }
        }
        
        return searchResults;

	}
	
	private void formatData() {
		// TODO 
		
	}
	
	private void storeDataToFile() {
		
		
	}
	
	private static Properties loadPropertiesFile() {
	
		Properties prop = new Properties();
		
		 try {
			 InputStream input = LDAPSearch.class.getClassLoader().getResourceAsStream("config.properties");
	          //load a properties file from class path, inside static method
			 prop.load(input);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
         
        return prop;
	}

	
	
    public static void main( String[] args ) {
        
        int searchScope = LDAPConnection.SCOPE_SUB;
        
    	try {
    		
    		   Properties prop = LDAPSearch.loadPropertiesFile();
                
               String loginDN = prop.getProperty("loginDN");
               String password = prop.getProperty("password");
               String searchBase = prop.getProperty("searchBase"); 
               String serverHost = prop.getProperty("serverHost"); ;
               int port = Integer.parseInt(prop.getProperty("port"));
               String searchFilter = prop.getProperty("searchFilter");
                
               System.out.println(loginDN+" "+password+" "+searchBase+" "+serverHost+" "+port+" "+searchFilter);
    		   
    		   LDAPSearch ldapSearch = new LDAPSearch();
    		   
    		   LDAPConnection connection = ldapSearch.getConnection(loginDN, password, serverHost, port);
    		   
    		   LDAPSearchResults results = ldapSearch.searchFromLDAP(connection,searchBase,searchScope,searchFilter);
    		   
    		   System.out.println("output results -> "+results);
    		   System.out.println("Hi.............");
    		   ldapSearch.formatData();
    		   
    		   ldapSearch.storeDataToFile();
    		   
    		   
		} catch(LDAPException e ) {
			
			e.printStackTrace();
		    System.out.println("Error " + e.toString() );
		    
        } catch (UnsupportedEncodingException e) {
        	
			e.printStackTrace();
			System.out.println("Error " + e.toString() );
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Error " + e.toString() );
		}
    }

	
}


