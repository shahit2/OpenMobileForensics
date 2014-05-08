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
import org.sleuthkit.autopsy.ingest.DataSourceIngestModuleProgress;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestModuleAdapter;
import org.sleuthkit.autopsy.ingest.IngestModuleReferenceCounter;

/**
 * Sample data source ingest module that doesn't do much. Demonstrates per
 * ingest job module settings, use of a subset of the available ingest services
 * and thread-safe sharing of per ingest job data.
 */
class AndroidDSIngestModule extends IngestModuleAdapter implements DataSourceIngestModule {

    private static final HashMap<Long, Long> fileCountsForIngestJobs = new HashMap<>();
    private final boolean skipKnownFiles;
    private IngestJobContext context = null;
    private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();

    AndroidDSIngestModule(AndroidModuleIngestJobSettings settings) {
        this.skipKnownFiles = settings.skipKnownFiles();

    }

    @Override
    public void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;

    }

    @Override
    public ProcessResult process(Content dataSource, DataSourceIngestModuleProgress progressBar) {
        // There are two tasks to do. Set the the progress bar to determinate 
        // and set the remaining number of work units to be completed to two.
        progressBar.switchToDeterminate(2);
        AndroidFindContacts FindContacts = new AndroidFindContacts();
        FindContacts.FindContacts();
        progressBar.progress(1);
        return IngestModule.ProcessResult.OK;
    }

    @Override
    public void shutDown(boolean ingestJobCancelled) {
 
    }

    
}
