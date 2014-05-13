/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sleuthkit.openmobileforensics.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


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
    public void setUp() {
        String output = "";
        String path = "C:\\Users\\tshahi\\Documents\\callogtest\\Export\\90-com.google.android.location\\files\\cache.cell";
        File file = new File(path);


        byte[] bytes;
        try {
            InputStream inputStream = new FileInputStream(file);
            long length = file.length();
            bytes = new byte[(int) 2]; // version
            inputStream.read(bytes);
            System.out.println(new BigInteger(bytes).intValue());
            bytes = new byte[(int) 2];
            inputStream.read(bytes); //number of location entries

            System.out.println(new BigInteger(bytes).intValue());
            int iterations = new BigInteger(bytes).intValue();
            for (int i = 0; i < iterations; i++) { //loop through every entry
                bytes = new byte[(int) 2];
                inputStream.read(bytes);
                bytes = new byte[(int) 1];
                inputStream.read(bytes);
                while (new BigInteger(bytes).intValue() != 0) //pass through non important values until the start of accuracy
                {
                    inputStream.read(bytes);
                }
                bytes = new byte[(int) 3];
                inputStream.read(bytes);
                System.out.println(new BigInteger(bytes).intValue());// accuracy
                bytes = new byte[(int) 4];
                inputStream.read(bytes);
                System.out.println(new BigInteger(bytes).intValue()); //conf
                bytes = new byte[(int) 8];
                inputStream.read(bytes);
                System.out.println(toDouble(bytes)); //lat
                bytes = new byte[(int) 8];
                inputStream.read(bytes);
                System.out.println(toDouble(bytes)); //long
                bytes = new byte[(int) 8];
                inputStream.read(bytes);
                System.out.println(new BigInteger(bytes).floatValue()); //time
            }

        } catch (Exception e) {
            System.out.println("Error is:" + e.getMessage());
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
 public static String toText(String info)throws UnsupportedEncodingException{
        byte[] encoded = info.getBytes();
        String text = new String(encoded, "UTF-8");
        System.out.println("print: "+text);
        return text;
    }
    static String readFile(String path, Charset encoding) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
    public static void processBuffer(byte[] buffer, int start, int end) {
        System.out.write(buffer, start, end);
    }
    public static int byteArrayToInt(byte[] b) {
    final ByteBuffer bb = ByteBuffer.wrap(b);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    return bb.getInt();
}
    public static double toDouble(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
}
}