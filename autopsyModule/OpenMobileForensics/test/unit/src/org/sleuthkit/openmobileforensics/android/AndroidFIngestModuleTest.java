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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;


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
        String result= "";
        String wrapper="1PVKy5OAa34c4UmycFmAUg";
        String s ="EhYxUFZLeTVPQWEzNGM0VW15Y0ZtQVVnGAAiBU5pZ2h0gAEAqgE6CgRIYW5rEgpMaXZpbmdzdG9uGhYxUFZLeTVPQWEzNGM0VW15Y0ZtQVVnIgoKBgoAEgAaABIAKgAwB7ABr/ypn5MouAEMwAEB0AEA6AGk3qmfkyjIAgPQAgDqAgIyM8gDANAD8oL5oQTYAwCwBAA=";
        byte[] decoded = Base64.decodeBase64(s);
        try{
        String Z= new String (decoded,"UTF-8");
       // System.out.println(new String(decoded, "UTF-8") );
        result = Z.split(wrapper)[1];
        }catch(Exception e){
            e.printStackTrace();
        }
       System.out.println(result);
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