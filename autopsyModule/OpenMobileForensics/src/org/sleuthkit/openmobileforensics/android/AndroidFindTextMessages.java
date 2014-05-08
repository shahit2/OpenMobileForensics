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
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.ReadContentInputStream;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import static org.sleuthkit.openmobileforensics.android.AndroidFindContacts.copyFileUsingStream;


 class AndroidFindTextMessages {
     private Connection connection = null;
    private ResultSet resultSet = null;
    private Statement statement = null;
    private String dbPath = "";
    private long fileId = 0;
    private java.io.File jFile = null;
    List<AbstractFile> absFiles;
    private String moduleName= AndroidIngestModuleFactory.getModuleName();
    
    void FindTexts() {
        try {
            SleuthkitCase skCase = Case.getCurrentCase().getSleuthkitCase();
            absFiles = skCase.findAllFilesWhere("name ='mmssms.db'"); //get exact file name
            if (absFiles.isEmpty()) {
                return;
            }
            for (AbstractFile AF : absFiles) {
                try {
                    jFile = new java.io.File(Case.getCurrentCase().getTempDirectory(), AF.getName());
                    copyFileUsingStream(AF, jFile); //extract the abstract file to the case's TEMP dir
                    dbPath = jFile.toString(); //path of file as string
                    fileId = AF.getId();
                    FindTextsInDB(dbPath, fileId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (TskCoreException e) {
            e.printStackTrace();
        }
    }
     private void FindTextsInDB(String DatabasePath, long fId) {
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
                        "Select address,date,type,subject,body FROM sms;");

                BlackboardArtifact bba;               
                bba = f.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_MESSAGE);
                String address; // may be phone number, or other addresses
                String date;//unix time
                String type; // message received in inbox = 1, message sent = 2
                String subject;//message subject
                String body; //message body
                while (resultSet.next()) {
                    address = resultSet.getString("address");
                    date = resultSet.getString("date");
                    type = resultSet.getString("type");
                    subject = resultSet.getString("subject");
                    body = resultSet.getString("body");
                    
                    bba = f.newArtifact(BlackboardArtifact.ARTIFACT_TYPE.TSK_MESSAGE); //create Message artifact and then add attributes from result set.
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_PHONE_NUMBER.getTypeID(), moduleName, address));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME.getTypeID(), moduleName, date));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DIRECTION.getTypeID(), moduleName, type));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_SUBJECT.getTypeID(), moduleName, subject));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_TEXT.getTypeID(), moduleName, body));
                    bba.addAttribute(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_CATEGORY.getTypeID(), moduleName,"SMS Message" ));

                }
//Test code
//                for (BlackboardArtifact artifact : skCase.getBlackboardArtifacts(BlackboardArtifact.ARTIFACT_TYPE.TSK_MESSAGE)) {
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

    public static void copyFileUsingStream(AbstractFile file, File jFile) throws IOException {
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
}
