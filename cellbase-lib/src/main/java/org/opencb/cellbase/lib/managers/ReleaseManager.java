/*
 * Copyright 2015-2020 OpenCB
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

package org.opencb.cellbase.lib.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.opencb.cellbase.core.config.CellBaseConfiguration;
import org.opencb.cellbase.core.exception.CellBaseException;
import org.opencb.cellbase.core.release.DataRelease;
import org.opencb.cellbase.core.result.CellBaseDataResult;
import org.opencb.cellbase.lib.impl.core.ReleaseMongoDBAdaptor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReleaseManager extends AbstractManager {
    private ReleaseMongoDBAdaptor releaseDBAdaptor;

    public ReleaseManager(String databaseName, CellBaseConfiguration configuration) throws CellBaseException {
        super(databaseName, configuration);

        init();
    }

    public ReleaseManager(String species, String assembly, CellBaseConfiguration configuration) throws CellBaseException {
        super(species, assembly, configuration);

        init();
    }

    private void init() {
        releaseDBAdaptor = dbAdaptorFactory.getReleaseDBAdaptor();
    }

    public CellBaseDataResult<DataRelease> getReleases() {
        return releaseDBAdaptor.getAll();
    }

    public DataRelease createRelease() throws JsonProcessingException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        // If collection release does not exist, it has to be created from zero (release 1), otherwise the biggest release
        // will be used to increment the release number and then create the new document release with the current date
        DataRelease lastRelease = null;
        CellBaseDataResult<DataRelease> releaseResult = getReleases();
        if (CollectionUtils.isNotEmpty(releaseResult.getResults())) {
            for (DataRelease dataRelease : releaseResult.getResults()) {
                if (lastRelease == null || dataRelease.getRelease() > lastRelease.getRelease()) {
                    lastRelease = dataRelease;
                }
            }
        }

        // Is it the first release?
        if (lastRelease == null) {
            // Create the first release, collections and sources are empty
            lastRelease = new DataRelease()
                    .setRelease(1)
                    .setActiveByDefault(false)
                    .setDate(sdf.format(new Date()));
            releaseDBAdaptor.insert(lastRelease);
        } else {
            // Check if collections are empty, a new data release will be created only if collections is not empty
            if (MapUtils.isNotEmpty(lastRelease.getCollections())) {
                // Increment the release number, only if the previous release has collections
                lastRelease.setRelease(lastRelease.getRelease() + 1)
                        .setActiveByDefault(false)
                        .setDate(sdf.format(new Date()));
                // Write it to the database
                releaseDBAdaptor.insert(lastRelease);
            } else {
                lastRelease = null;
            }
        }
        return lastRelease;
    }

    public void activeByDefault(int release) throws JsonProcessingException {
        // Gel all releases and check if the input release exists
        DataRelease prevActive = null;
        DataRelease newActive = null;
        CellBaseDataResult<DataRelease> releaseResult = getReleases();
        if (CollectionUtils.isEmpty(releaseResult.getResults())) {
            // Nothing to do, maybe exception or warning
            return;
        }
        for (DataRelease dataRelease : releaseResult.getResults()) {
            if (dataRelease.isActiveByDefault()) {
                prevActive = dataRelease;
            } else if (dataRelease.getRelease() == release) {
                newActive = dataRelease;
            }
        }
        if (prevActive != null && newActive != null && newActive.getRelease() == prevActive.getRelease()) {
            // Nothing to do
            return;
        }

        // Change active by default
        if (prevActive != null) {
            prevActive.setActiveByDefault(false);
            releaseDBAdaptor.update(prevActive.getRelease(), "activeByDefault", prevActive.isActiveByDefault());
        }
        if (newActive != null) {
            newActive.setActiveByDefault(true);
            releaseDBAdaptor.update(newActive.getRelease(), "activeByDefault", newActive.isActiveByDefault());
        }
    }

    public String getMaintenanceFlagFile() {
        return configuration.getMaintenanceFlagFile();
    }

    public String getMaintainerContact() {
        return configuration.getMaintainerContact();
    }
}
