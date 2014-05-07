/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sleuthkit.openmobileforensics.android;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.*;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.SleuthkitCase;
/**
 *
 * @author tshahi
 */
public class AndroidFIngestModuleTest {
    
    public AndroidFIngestModuleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() 
    {
   
   try {
       Class.forName("org.sqlite.JDBC");
   }catch(Exception e){
       System.out.println("sql lite drivers not loaded");
   }

        Connection connection = null;  
        ResultSet resultSet = null;  
        Statement statement = null;  
    
        try {  
        
            
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\tshahi\\Desktop\\contacts2.db");  
            statement = connection.createStatement();  

            // get display_name, mimetype(email or phone number) and data1 (phonenumber or email address depending on mimetype)
            //sorted by name, so phonenumber/email would be consecutive for a person if they exist.
            resultSet = statement.executeQuery(
                    "SELECT mimetype,data1, name_raw_contact.display_name AS display_name \n" +
                    "FROM raw_contacts JOIN contacts ON (raw_contacts.contact_id=contacts._id) \n" +
                    "JOIN raw_contacts AS name_raw_contact ON(name_raw_contact_id=name_raw_contact._id) "+
                    "LEFT OUTER JOIN data ON (data.raw_contact_id=raw_contacts._id) \n" +
                    "LEFT OUTER JOIN mimetypes ON (data.mimetype_id=mimetypes._id) \n" +
                    "WHERE mimetype = 'vnd.android.cursor.item/phone_v2' OR mimetype = 'vnd.android.cursor.item/email_v2'\n" +
                    "ORDER BY name_raw_contact.display_name ASC;");  

            while (resultSet.next()) {  
                System.out.println(resultSet.getString("data1") + resultSet.getString("mimetype") + resultSet.getString("display_name"));
               // BlackboardArtifact.ARTIFACT_TYPE.TSK_CONTACT;
              //  BlackboardArtifact bba = f.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_METADATA_EXIF);
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                resultSet.close();  
                statement.close();  
                connection.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
       
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of startUp method, of class CyberIngestModule.
     */
    @Test
    public void testStartUp() throws Exception {

    }

   
}