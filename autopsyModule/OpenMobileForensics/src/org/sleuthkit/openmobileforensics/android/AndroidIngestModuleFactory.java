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


import org.openide.util.lookup.ServiceProvider;
import org.openide.util.NbBundle;

import org.sleuthkit.autopsy.ingest.IngestModuleFactory;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleFactoryAdapter;
import org.sleuthkit.autopsy.ingest.IngestModuleGlobalSettingsPanel;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;


@ServiceProvider(service = IngestModuleFactory.class) //  
public class AndroidIngestModuleFactory extends IngestModuleFactoryAdapter {

    private static final String VERSION_NUMBER = "1.0.0";

    // This class method allows the ingest module instances created by this 
    // factory to use the same display name that is provided to the Autopsy
    // ingest framework by the factory.
    static String getModuleName() {
        return NbBundle.getMessage(AndroidIngestModuleFactory.class, "AndroidIngestModuleFactory.moduleName");
    }

    /**
     * Gets the display name that identifies the family of ingest modules the
     * factory creates. Autopsy uses this string to identify the module in user
     * interface components and log messages. The module name must be unique. so
     * a brief but distinctive name is recommended.
     *
     * @return The module family display name.
     */
    @Override
    public String getModuleDisplayName() {
        return getModuleName();
    }

    /**
     * Gets a brief, user-friendly description of the family of ingest modules
     * the factory creates. Autopsy uses this string to describe the module in
     * user interface components.
     *
     * @return The module family description.
     */
    @Override
    public String getModuleDescription() {
        return NbBundle.getMessage(AndroidIngestModuleFactory.class, "AndroidIngestModuleFactory.moduleDescription");
    }

    /**
     * Gets the version number of the family of ingest modules the factory
     * creates.
     *
     * @return The module family version number.
     */
    @Override
    public String getModuleVersionNumber() {
        return VERSION_NUMBER;
    }

    /**
     * Queries the factory to determine if it provides a user interface panel to
     * allow a user to change settings that are used by all instances of the
     * family of ingest modules the factory creates. For example, the Autopsy
     * core hash lookup ingest module factory provides a global settings panel
     * to import and create hash databases. The hash databases are then enabled
     * or disabled per ingest job using an ingest job settings panel. If the
     * module family does not have global settings, the factory may extend
     * IngestModuleFactoryAdapter to get an implementation of this method that
     * returns false.
     *
     * @return True if the factory provides a global settings panel.
     */
    @Override
    public boolean hasGlobalSettingsPanel() {
        return false;
    }

    /**
     * Gets a user interface panel that allows a user to change settings that
     * are used by all instances of the family of ingest modules the factory
     * creates. For example, the Autopsy core hash lookup ingest module factory
     * provides a global settings panel to import and create hash databases. The
     * imported hash databases are then enabled or disabled per ingest job using
     * ingest an ingest job settings panel. If the module family does not have a
     * global settings, the factory may extend IngestModuleFactoryAdapter to get
     * an implementation of this method that throws an
     * UnsupportedOperationException.
     *
     * @return A global settings panel.
     */
    @Override
    public IngestModuleGlobalSettingsPanel getGlobalSettingsPanel() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the default per ingest job settings for instances of the family of
     * ingest modules the factory creates. For example, the Autopsy core hash
     * lookup ingest modules family uses hash databases imported or created
     * using its global settings panel. All of the hash databases are enabled by
     * default for an ingest job. If the module family does not have per ingest
     * job settings, the factory may extend IngestModuleFactoryAdapter to get an
     * implementation of this method that returns an instance of the
     * NoIngestModuleJobSettings class.
     *
     * @return The default ingest job settings.
     */
    @Override
    public IngestModuleIngestJobSettings getDefaultIngestJobSettings() {
        return new AndroidModuleIngestJobSettings();
    }

    /**
     * Queries the factory to determine if it provides user a interface panel to
     * allow a user to make per ingest job settings for instances of the family
     * of ingest modules the factory creates. For example, the Autopsy core hash
     * lookup ingest module factory provides an ingest job settings panels to
     * enable or disable hash databases per ingest job. If the module family
     * does not have per ingest job settings, the factory may extend
     * IngestModuleFactoryAdapter to get an implementation of this method that
     * returns false.
     *
     * @return True if the factory provides ingest job settings panels.
     */
    @Override
    public boolean hasIngestJobSettingsPanel() {
        return true;
    }

    /**
     * Gets a user interface panel that can be used to set per ingest job
     * settings for instances of the family of ingest modules the factory
     * creates. For example, the core hash lookup ingest module factory provides
     * an ingest job settings panel to enable or disable hash databases per
     * ingest job. If the module family does not have per ingest job settings,
     * the factory may extend IngestModuleFactoryAdapter to get an
     * implementation of this method that throws an
     * UnsupportedOperationException.
     *
     * @param setting Per ingest job settings to initialize the panel.
     * @return An ingest job settings panel.
     */
    @Override
    public IngestModuleIngestJobSettingsPanel getIngestJobSettingsPanel(IngestModuleIngestJobSettings settings) {
        if (!(settings instanceof AndroidModuleIngestJobSettings)) {
            throw new IllegalArgumentException("Expected settings argument to be instanceof AndroidModuleIngestJobSettings");
        }
        return new AndroidIngestJobSettingsPanel((AndroidModuleIngestJobSettings) settings);
    }

    /**
     * Queries the factory to determine if it is capable of creating data source
     * ingest modules. If the module family does not include data source ingest
     * modules, the factory may extend IngestModuleFactoryAdapter to get an
     * implementation of this method that returns false.
     *
     * @return True if the factory can create data source ingest modules.
     */
    @Override
    public boolean isDataSourceIngestModuleFactory() {
        return true;
    }

    @Override
    public DataSourceIngestModule createDataSourceIngestModule(IngestModuleIngestJobSettings settings) {
        if (!(settings instanceof AndroidModuleIngestJobSettings)) {
            throw new IllegalArgumentException("Expected settings argument to be instanceof AndroidModuleIngestJobSettings");
        }
        return new AndroidDSIngestModule((AndroidModuleIngestJobSettings) settings);
    }

}
