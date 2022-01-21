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

package org.opencb.cellbase.lib.builders;

import org.opencb.biodata.models.core.SpliceScore;
import org.opencb.biodata.models.core.SpliceScoreAlternate;
import org.opencb.cellbase.core.serializer.CellBaseFileSerializer;
import org.opencb.cellbase.lib.EtlCommons;
import org.opencb.commons.utils.FileUtils;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class SpliceBuilder extends CellBaseBuilder {

    private CellBaseFileSerializer fileSerializer;
    private Path spliceDir;

    public SpliceBuilder(Path spliceDir, CellBaseFileSerializer serializer) {
        super(serializer);

        this.fileSerializer = serializer;
        this.spliceDir = spliceDir;

        logger = LoggerFactory.getLogger(SpliceBuilder.class);
    }

    @Override
    public void parse() throws Exception {
        // Check input folder
        FileUtils.checkPath(spliceDir);

        logger.info("Parsing splice files...");

        Path mmsplicePath = spliceDir.resolve(EtlCommons.MMSPLICE_SUBDIRECTORY);
        if (mmsplicePath.toFile().exists()) {
            logger.info("Parsing MMSplice data...");
            mmspliceParser(mmsplicePath);
        } else {
            logger.debug("MMSplice data not found: " + mmsplicePath.toString());
        }

        logger.info("Parsing splice scores finished.");
    }

    private void mmspliceParser(Path mmsplicePath) throws IOException {
        // Check output folder: MMSplice
        Path mmspliceOutFolder = fileSerializer.getOutdir().resolve(EtlCommons.MMSPLICE_SUBDIRECTORY);
        if (!mmspliceOutFolder.toFile().exists()) {
            mmspliceOutFolder.toFile().mkdirs();
        }

        // RocksDB to avoid duplicated data
        File rocksDBFile = new File("/tmp/mmsplice.rocksdb");
        if (rocksDBFile.exists()) {
            org.apache.commons.io.FileUtils.deleteDirectory(rocksDBFile);
        }
        RocksDbManager rocksDbManager = new RocksDbManager();
        RocksDB rocksDB = rocksDbManager.getDBConnection(rocksDBFile.getAbsolutePath());

        for (File file : mmsplicePath.toFile().listFiles()) {
            logger.info("Parsing MMSplice file {} ...", file.getName());
            try (BufferedReader in = FileUtils.newBufferedReader(file.toPath())) {
                String line = in.readLine();
                if (line != null) {
                    String[] labels = line.split(",");

                    SpliceScore spliceScore = null;

                    // Main loop
                    while ((line = in.readLine()) != null) {
                        String[] fields = line.split(",");
                        String[] idFields = fields[0].split(":");

                        String chrom = idFields[0];
                        int position = Integer.parseInt(idFields[1]);
                        String ref = idFields[2];
                        String alt = idFields[3].replace("'", "").replace("[", "").replace("]", "");

                        // Transcript
                        String transcript = fields[5];

                        // Check for duplicated lines
                        String uid = chrom + ":" + position + ":" + ref + ":" + alt + ":" + transcript;
                        if (rocksDB.get(uid.getBytes()) != null) {
                            continue;
                        }
                        rocksDB.put(uid.getBytes(), "1".getBytes());

                        // Create alternate splice score to be added further
                        SpliceScoreAlternate scoreAlt = new SpliceScoreAlternate(alt, new HashMap<>());
                        for (int i = 6; i < labels.length; i++) {
                            scoreAlt.getScores().put(labels[i], Double.parseDouble(fields[i]));
                        }

                        // Create splice score
                        if (spliceScore != null
                                && spliceScore.getChromosome().equals(chrom)
                                && spliceScore.getPosition() == position
                                && spliceScore.getRefAllele().equals(ref)
                                && spliceScore.getTranscriptId().equals(transcript)) {
                            spliceScore.getAlternates().add(scoreAlt);
                        } else {
                            if (spliceScore != null) {
                                // Write the currant splice score
                                fileSerializer.serialize(spliceScore, EtlCommons.MMSPLICE_SUBDIRECTORY + "/mmsplice_chr"
                                        + spliceScore.getChromosome());
                            }

                            // And prepare the new splice score
                            spliceScore = new SpliceScore();
                            spliceScore.setChromosome(chrom);
                            spliceScore.setPosition(position);
                            spliceScore.setRefAllele(ref);
                            spliceScore.setGeneId(fields[3]);
                            spliceScore.setGeneName(fields[4]);
                            spliceScore.setTranscriptId(transcript);
                            spliceScore.setExonId(fields[2]);
                            spliceScore.setSource("mmsplice");
                            spliceScore.setAlternates(new ArrayList<>());

                            spliceScore.getAlternates().add(scoreAlt);
                        }
                    }

                    if (spliceScore != null) {
                        // Write the last splice score
                        fileSerializer.serialize(spliceScore, EtlCommons.MMSPLICE_SUBDIRECTORY + "/mmsplice_chr"
                                + spliceScore.getChromosome());
                    }
                }
            } catch (IOException | RocksDBException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        rocksDB.close();
    }
}
