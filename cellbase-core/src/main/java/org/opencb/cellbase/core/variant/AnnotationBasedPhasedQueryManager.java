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

package org.opencb.cellbase.core.variant;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.cellbase.core.result.CellBaseDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AnnotationBasedPhasedQueryManager<T> extends PhasedQueryManager {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<CellBaseDataResult<Variant>> run(List<Variant> variantList,
                                                 List<CellBaseDataResult<Variant>> variantCellBaseDataResultList) {
        // Go through all input variants and their corresponding query results
        for (int j = 0; j < variantCellBaseDataResultList.size(); j++) {
            CellBaseDataResult<Variant> variantCellBaseDataResult = variantCellBaseDataResultList.get(j);
            if (variantCellBaseDataResult != null && variantCellBaseDataResult.getResults() != null
                    && !variantCellBaseDataResult.getResults().isEmpty()) {
                // Variants are normalised and data from each of the sources (COSMIC, ClinVar, DOCM, etc.) integrated
                // during the build process. Only one variant record should be present per assembly.
                if (variantCellBaseDataResult.getResults().size() > 1) {
                    logger.warn("More than one result found either the clinical_variants or variation collection"
                            + "for variant " + variantCellBaseDataResult.getId() + ". Arbitrarily selecting the first one. "
                            + "Please, check.");
                }

                Variant matchedVariant = variantCellBaseDataResult.getResults().get(0);
                Variant queryVariant = variantList.get(j);
                List<T> annotationObjectList = getAnnotationObjectList(matchedVariant);
                // Phase is stored at the evidence entry/population frequency level, e.g.: there might be two ClinVar
                // RCVs for one variant:
                //   - In the first the variant is submitted as part of an MNV and therefore it is phased
                //   - In the second one the variant is submitted singleton and therefore it is not phased
                // both RCVs will be integrated in the same Variant object after decomposition as separate EvidenceEntry
                // objects, each with its corresponding phase information
                int i = 0;
                while (i < annotationObjectList.size()) {
                    T annotationObject = annotationObjectList.get(i);
                    List<Variant> databaseHaplotype = getHaplotype(annotationObject, matchedVariant);
                    // Haplotype empty if EvidenceEntry/PopulationFrequency is not phased
                    if (databaseHaplotype.isEmpty()) {
                        i++;
                    } else {
                        // Sample   Cellbase  Match
                        // -------------------------------
                        // SNV      MNV       X
                        // MNV      MNV       ✓
                        // Missing genotypes in the input list will be considered as wildcards towards finding a
                        // matching haplotype (MNV) in the input list, since otherwise the clinical variant would not be
                        // returned
                        if (sameHaplotype(queryVariant, variantList, databaseHaplotype)) {
                            i++;
                        } else {
                            annotationObjectList.remove(i);
                        }
                        // Sample   Cellbase  Match
                        // -------------------------------
                        // SNV      SNV       ✓
                        // MNV      SNV       ✓
                    }
                }

                // Remove whole variant from the query result object if ended up without any evidence entry
                if (annotationObjectList.isEmpty()) {
                    reset(variantCellBaseDataResult);
                }
            }
        }

        return variantCellBaseDataResultList;
    }

    protected abstract List<Variant> getHaplotype(T annotationObject, Variant variant);

    protected abstract List<T> getAnnotationObjectList(Variant variant);

}
