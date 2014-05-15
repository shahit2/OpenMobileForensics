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

import java.util.HashMap;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModuleProgress;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestMessage;
import org.sleuthkit.autopsy.ingest.IngestModuleAdapter;
import org.sleuthkit.autopsy.ingest.IngestModuleReferenceCounter;
import org.sleuthkit.autopsy.ingest.IngestServices;


class AndroidIngestModule extends IngestModuleAdapter implements DataSourceIngestModule {

    private static final HashMap<Long, Long> fileCountsForIngestJobs = new HashMap<>();
    private IngestJobContext context = null;
    private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();
    private static final Logger logger = Logger.getLogger(AndroidIngestModule.class.getName());
    private IngestServices services = IngestServices.getInstance();

    @Override
    public void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;

    }

    @Override
    public ProcessResult process(Content dataSource, DataSourceIngestModuleProgress progressBar) {
        // There are two tasks to do. Set the the progress bar to determinate 
        // and set the remaining number of work units to be completed to two.
        progressBar.switchToDeterminate(8);
        try{
        ContactAnalyzer FindContacts = new ContactAnalyzer();
        FindContacts.findContacts();
        progressBar.progress(1);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        CallLogAnalyzer FindCallLogs = new CallLogAnalyzer();
        FindCallLogs.findCallLogs();
        progressBar.progress(2);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        TextMessageAnalyzer FindTexts = new TextMessageAnalyzer();
        FindTexts.findTexts();
        progressBar.progress(3);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        TangoMessageAnalyzer FindTangoMessages = new TangoMessageAnalyzer();
        FindTangoMessages.findTangoMessages();
        progressBar.progress(4);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        WWFMessageAnalyzer FindWWFMessages = new WWFMessageAnalyzer();
        FindWWFMessages.findWWFMessages();
        progressBar.progress(5);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        GoogleMapLocationAnalyzer FindGoogleMapLocations = new GoogleMapLocationAnalyzer();
        FindGoogleMapLocations.findGeoLocations();
        progressBar.progress(6);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        BrowserLocationAnalyzer FindBrowserLocations = new BrowserLocationAnalyzer();
        FindBrowserLocations.findGeoLocations();
        progressBar.progress(7);
        if (context.isJobCancelled())return IngestModule.ProcessResult.OK;
        
        CacheLocationAnalyzer FindCacheLocations = new CacheLocationAnalyzer();
        FindCacheLocations.findGeoLocations();
        progressBar.progress(8);
        final IngestMessage inboxMsg = IngestMessage.createMessage(IngestMessage.MessageType.INFO, AndroidModuleFactory.getModuleName(),"hi");
        services.postMessage(inboxMsg);
     
        }catch(Exception e){
            final IngestMessage inboxMsg = IngestMessage.createMessage(IngestMessage.MessageType.INFO, AndroidModuleFactory.getModuleName(),"There were errors");
             services.postMessage(inboxMsg);
             return IngestModule.ProcessResult.ERROR;
        }
           return IngestModule.ProcessResult.OK;
    }

    @Override
    public void shutDown(boolean ingestJobCancelled) {
 
    }

    
}
