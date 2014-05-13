/*
 * Autopsy Forensic Browser
 *
 * Copyright 2014 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.openmobileforensics.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.ReadContentInputStream;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import static org.sleuthkit.openmobileforensics.android.AndroidFindCallLogs.copyFileUsingStream;

 class AndroidFindTangoMessages {
    private Connection connection = null;
    private ResultSet resultSet = null;
    private Statement statement = null;
    private String dbPath = "";
    private long fileId = 0;
    private java.io.File jFile = null;
    private String moduleName= AndroidIngestModuleFactory.getModuleName();
    public void FindTangoMessages() {
        List<AbstractFile> absFiles;
        try {
            SleuthkitCase skCase = Case.getCurrentCase().getSleuthkitCase();
            absFiles = skCase.findAllFilesWhere("name ='tc.db' "); //get exact file names
            if (absFiles.isEmpty()) {
                return;
            }
            for (AbstractFile AF : absFiles) {
                try {
                    jFile = new java.io.File(Case.getCurrentCase().getTempDirectory(), AF.getName());
                    copyFileUsingStream(AF, jFile); //extract the abstract file to the case's TEMP dir
                    dbPath = jFile.toString(); //path of file as string
                    fileId = AF.getId();
                    FindTangoMessagesInDB(dbPath, fileId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (TskCoreException e) {
            e.printStackTrace();
        }
        
    }
    private void FindTangoMessagesInDB(String DatabasePath, long fId) {
        if (DatabasePath == null || DatabasePath.isEmpty()) {
            return;
        }
        try {
            Class.forName("org.sqlite.JDBC"); //load JDBC driver
            connection = DriverManager.getConnection("jdbc:sqlite:" + DatabasePath);
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        Case currentCase = Case.getCurrentCase();
        SleuthkitCase skCase = currentCase.getSleuthkitCase();
        try {
            AbstractFile f = skCase.getAbstractFileById(fId);
            try {
                resultSet = statement.executeQuery(
                        "Select conv_id, create_time,direction,payload FROM messages ORDER BY create_time DESC;");

                BlackboardArtifact bba;
                String conv_id; // seems to wrap around the message found in payload after decoding from base-64
                String create_time; // unix time
                String direction; // 1 incoming, 2 outgoing
                String payload; // seems to be a base64 message wrapped by the conv_id
              

                while (resultSet.next()) {
                    conv_id = resultSet.getString("conv_id");
                    create_time = resultSet.getString("create_time");
                    direction = resultSet.getString("direction");
                    payload = resultSet.getString("payload");

                    bba = f.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_MESSAGE); //create a call log and then add attributes from result set.
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME.getTypeID(), moduleName, create_time));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DIRECTION.getTypeID(), moduleName, direction));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_TEXT.getTypeID(), moduleName, DecodeMessage(conv_id,payload)));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_MESSAGE_TYPE.getTypeID(), moduleName,"Tango Message" ));

                }
//Test code
//                for (BlackboardArtifact artifact : skCase.getBlackboardArtifacts(BlackboardArtifact.ARTIFACT_TYPE.TSK_CALLLOG)) {
//                    System.out.println(artifact.getAttributes().toString());
//                }

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void copyFileUsingStream(AbstractFile file, File jFile) throws IOException {
        InputStream is = new ReadContentInputStream(file);
        OutputStream os = new FileOutputStream(jFile);
        byte[] buffer = new byte[8192];
        int length;
        try {
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }

        } finally {
            is.close();
            os.close();
        }
    }
   private String DecodeMessage(String wrapper, String message)
   {
       String result= "";
       byte[] decoded = Base64.decodeBase64(message);
        try{
        String Z= new String (decoded,"UTF-8");
        result = Z.split(wrapper)[1];
        }catch(Exception e){
            e.printStackTrace();
        }     
       return result;
   }
}
