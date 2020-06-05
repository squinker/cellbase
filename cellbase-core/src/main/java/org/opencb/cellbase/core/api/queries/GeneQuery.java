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

package org.opencb.cellbase.core.api.queries;

import org.apache.commons.collections.CollectionUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.cellbase.core.BioUtils;

import java.util.List;
import java.util.Map;

public class GeneQuery extends AbstractQuery {

    @QueryParameter(id = "id")
    private List<String> ids;
    @QueryParameter(id = "name")
    private List<String> names;
    @QueryParameter(id = "biotype")
    private List<String> biotypes;
    @QueryParameter(id = "supportLevel")
    private List<String> supportLevels;

    @QueryParameter(id = "region")
    private List<Region> regions;

    @QueryParameter(id = "transcripts.biotype")
    private List<String> transcriptsBiotype;
    @QueryParameter(id = "transcripts.xrefs")
    private List<String> transcriptsXrefs;
    @QueryParameter(id = "transcripts.id")
    private List<String> transcriptsId;
    @QueryParameter(id = "transcripts.name")
    private List<String> transcriptsName;

    @QueryParameter(id = "transcripts.annotationFlags")
    private LogicalList<String> transcriptsAnnotationFlags;
    @QueryParameter(id = "transcripts.tfbs.id")
    private LogicalList<String> transcriptsTfbsId;
    @QueryParameter(id = "transcripts.tfbs.pfmId")
    private LogicalList<String> transcriptsTfbsPfmId;
    @QueryParameter(id = "transcripts.tfbs.transcriptionFactors")
    private LogicalList<String> transcriptsTfbsTranscriptionFactors;
    @QueryParameter(id = "transcripts.annotation.ontologies.id")
    private LogicalList<String> annotationOntologiesId;

    @QueryParameter(id = "annotation.diseases.id")
    private LogicalList<String> annotationDiseasesId;
    @QueryParameter(id = "annotation.diseases.name")
    private LogicalList<String> annotationDiseasesName;
    @QueryParameter(id = "annotation.expression.tissue")
    private LogicalList<String> annotationExpressionTissue;
    @QueryParameter(id = "annotation.expression.value")
    private LogicalList<String> annotationExpressionValue;
    @QueryParameter(id = "annotation.drugs.name")
    private LogicalList<String> annotationDrugsName;
    @QueryParameter(id = "annotation.constraints.name",
            allowedValues = {"exac_oe_lof", "exac_pLI", "oe_lof", "oe_mis", "oe_syn"})
    // this has to be protected to allow validation
    protected LogicalList<String> annotationConstraintsName;
    @QueryParameter(id = "annotation.constraints.value", dependsOn = "annotation.constraints.name")
    protected LogicalList<String> annotationConstraintsValue;
    @QueryParameter(id = "mirna")
    private LogicalList<String> mirnas;

    public GeneQuery() {
    }

    public GeneQuery(Map<String, String> params) throws QueryException {
        super(params);
    }

    @Override
    protected void validateQuery() throws QueryException {
        if (CollectionUtils.isNotEmpty(biotypes)) {
            for (String biotype : biotypes) {
                if (!BioUtils.isValidBiotype(biotype)) {
                    throw new QueryException("Invalid biotype: '" + biotype + "'");
                }
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GeneQuery{");
        sb.append("ids=").append(ids);
        sb.append(", names=").append(names);
        sb.append(", biotypes=").append(biotypes);
        sb.append(", supportLevels=").append(supportLevels);
        sb.append(", regions=").append(regions);
        sb.append(", transcriptsBiotype=").append(transcriptsBiotype);
        sb.append(", transcriptsXrefs=").append(transcriptsXrefs);
        sb.append(", transcriptsId=").append(transcriptsId);
        sb.append(", transcriptsName=").append(transcriptsName);
        sb.append(", transcriptsAnnotationFlags=").append(transcriptsAnnotationFlags);
        sb.append(", transcriptsTfbsId=").append(transcriptsTfbsId);
        sb.append(", transcriptsTfbsPfmId=").append(transcriptsTfbsPfmId);
        sb.append(", transcriptsTfbsTranscriptionFactors=").append(transcriptsTfbsTranscriptionFactors);
        sb.append(", annotationOntologiesId=").append(annotationOntologiesId);
        sb.append(", annotationDiseasesId=").append(annotationDiseasesId);
        sb.append(", annotationDiseasesName=").append(annotationDiseasesName);
        sb.append(", annotationExpressionTissue=").append(annotationExpressionTissue);
        sb.append(", annotationExpressionValue=").append(annotationExpressionValue);
        sb.append(", annotationDrugsName=").append(annotationDrugsName);
        sb.append(", annotationConstraintsName=").append(annotationConstraintsName);
        sb.append(", annotationConstraintsValue=").append(annotationConstraintsValue);
        sb.append(", mirnas=").append(mirnas);
        sb.append('}');
        return sb.toString();
    }

    public List<String> getIds() {
        return ids;
    }

    public GeneQuery setIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public List<String> getNames() {
        return names;
    }

    public GeneQuery setNames(List<String> names) {
        this.names = names;
        return this;
    }

    public List<String> getBiotypes() {
        return biotypes;
    }

    public GeneQuery setBiotypes(List<String> biotypes) {
        this.biotypes = biotypes;
        return this;
    }

    public List<String> getSupportLevels() {
        return supportLevels;
    }

    public GeneQuery setSupportLevels(List<String> supportLevels) {
        this.supportLevels = supportLevels;
        return this;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public GeneQuery setRegions(List<Region> regions) {
        this.regions = regions;
        return this;
    }

    public List<String> getTranscriptsBiotype() {
        return transcriptsBiotype;
    }

    public GeneQuery setTranscriptsBiotype(List<String> transcriptsBiotype) {
        this.transcriptsBiotype = transcriptsBiotype;
        return this;
    }

    public List<String> getTranscriptsXrefs() {
        return transcriptsXrefs;
    }

    public GeneQuery setTranscriptsXrefs(List<String> transcriptsXrefs) {
        this.transcriptsXrefs = transcriptsXrefs;
        return this;
    }

    public List<String> getTranscriptsId() {
        return transcriptsId;
    }

    public GeneQuery setTranscriptsId(List<String> transcriptsId) {
        this.transcriptsId = transcriptsId;
        return this;
    }

    public List<String> getTranscriptsName() {
        return transcriptsName;
    }

    public GeneQuery setTranscriptsName(List<String> transcriptsName) {
        this.transcriptsName = transcriptsName;
        return this;
    }

    public LogicalList<String> getTranscriptsAnnotationFlags() {
        return transcriptsAnnotationFlags;
    }

    public GeneQuery setTranscriptsAnnotationFlags(LogicalList<String> transcriptsAnnotationFlags) {
        this.transcriptsAnnotationFlags = transcriptsAnnotationFlags;
        return this;
    }

    public LogicalList<String> getTranscriptsTfbsId() {
        return transcriptsTfbsId;
    }

    public GeneQuery setTranscriptsTfbsId(LogicalList<String> transcriptsTfbsId) {
        this.transcriptsTfbsId = transcriptsTfbsId;
        return this;
    }

    public LogicalList<String> getTranscriptsTfbsPfmId() {
        return transcriptsTfbsPfmId;
    }

    public GeneQuery setTranscriptsTfbsPfmId(LogicalList<String> transcriptsTfbsPfmId) {
        this.transcriptsTfbsPfmId = transcriptsTfbsPfmId;
        return this;
    }

    public LogicalList<String> getTranscriptsTfbsTranscriptionFactors() {
        return transcriptsTfbsTranscriptionFactors;
    }

    public GeneQuery setTranscriptsTfbsTranscriptionFactors(LogicalList<String> transcriptsTfbsTranscriptionFactors) {
        this.transcriptsTfbsTranscriptionFactors = transcriptsTfbsTranscriptionFactors;
        return this;
    }

    public LogicalList<String> getAnnotationOntologiesId() {
        return annotationOntologiesId;
    }

    public GeneQuery setAnnotationOntologiesId(LogicalList<String> annotationOntologiesId) {
        this.annotationOntologiesId = annotationOntologiesId;
        return this;
    }

    public LogicalList<String> getAnnotationDiseasesId() {
        return annotationDiseasesId;
    }

    public GeneQuery setAnnotationDiseasesId(LogicalList<String> annotationDiseasesId) {
        this.annotationDiseasesId = annotationDiseasesId;
        return this;
    }

    public LogicalList<String> getAnnotationDiseasesName() {
        return annotationDiseasesName;
    }

    public GeneQuery setAnnotationDiseasesName(LogicalList<String> annotationDiseasesName) {
        this.annotationDiseasesName = annotationDiseasesName;
        return this;
    }

    public LogicalList<String> getAnnotationExpressionTissue() {
        return annotationExpressionTissue;
    }

    public GeneQuery setAnnotationExpressionTissue(LogicalList<String> annotationExpressionTissue) {
        this.annotationExpressionTissue = annotationExpressionTissue;
        return this;
    }

    public LogicalList<String> getAnnotationExpressionValue() {
        return annotationExpressionValue;
    }

    public GeneQuery setAnnotationExpressionValue(LogicalList<String> annotationExpressionValue) {
        this.annotationExpressionValue = annotationExpressionValue;
        return this;
    }

    public LogicalList<String> getAnnotationDrugsName() {
        return annotationDrugsName;
    }

    public GeneQuery setAnnotationDrugsName(LogicalList<String> annotationDrugsName) {
        this.annotationDrugsName = annotationDrugsName;
        return this;
    }

    public LogicalList<String> getAnnotationConstraintsName() {
        return annotationConstraintsName;
    }

    public GeneQuery setAnnotationConstraintsName(LogicalList<String> annotationConstraintsName) {
        this.annotationConstraintsName = annotationConstraintsName;
        return this;
    }

    public LogicalList<String> getAnnotationConstraintsValue() {
        return annotationConstraintsValue;
    }

    public GeneQuery setAnnotationConstraintsValue(LogicalList<String> annotationConstraintsValue) {
        this.annotationConstraintsValue = annotationConstraintsValue;
        return this;
    }

    public LogicalList<String> getMirnas() {
        return mirnas;
    }

    public GeneQuery setMirnas(LogicalList<String> mirnas) {
        this.mirnas = mirnas;
        return this;
    }


    public static final class GeneQueryBuilder {
        protected Integer limit;
        protected Integer skip;
        protected Boolean count = false;
        protected String sort;
        protected Order order;
        protected String facet;
        protected List<String> includes;
        protected List<String> excludes;
        private List<String> ids;
        private List<String> names;
        private List<String> biotypes;
        private List<String> supportLevels;
        private List<Region> regions;
        private List<String> transcriptsBiotype;
        private List<String> transcriptsXrefs;
        private List<String> transcriptsId;
        private List<String> transcriptsName;
        private LogicalList<String> transcriptsAnnotationFlags;
        private LogicalList<String> transcriptsTfbsId;
        private LogicalList<String> transcriptsTfbsPfmId;
        private LogicalList<String> transcriptsTfbsTranscriptionFactors;
        private LogicalList<String> annotationOntologiesId;
        private LogicalList<String> annotationDiseasesId;
        private LogicalList<String> annotationDiseasesName;
        private LogicalList<String> annotationExpressionTissue;
        private LogicalList<String> annotationExpressionValue;
        private LogicalList<String> annotationDrugsName;
        private LogicalList<String> annotationConstraintsName;
        private LogicalList<String> annotationConstraintsValue;
        private LogicalList<String> mirnas;

        private GeneQueryBuilder() {
        }

        public static GeneQueryBuilder aGeneQuery() {
            return new GeneQueryBuilder();
        }

        public GeneQueryBuilder withIds(List<String> ids) {
            this.ids = ids;
            return this;
        }

        public GeneQueryBuilder withNames(List<String> names) {
            this.names = names;
            return this;
        }

        public GeneQueryBuilder withBiotypes(List<String> biotypes) {
            this.biotypes = biotypes;
            return this;
        }

        public GeneQueryBuilder withSupportLevels(List<String> supportLevels) {
            this.supportLevels = supportLevels;
            return this;
        }

        public GeneQueryBuilder withRegions(List<Region> regions) {
            this.regions = regions;
            return this;
        }

        public GeneQueryBuilder withTranscriptsBiotype(List<String> transcriptsBiotype) {
            this.transcriptsBiotype = transcriptsBiotype;
            return this;
        }

        public GeneQueryBuilder withTranscriptsXrefs(List<String> transcriptsXrefs) {
            this.transcriptsXrefs = transcriptsXrefs;
            return this;
        }

        public GeneQueryBuilder withTranscriptsId(List<String> transcriptsId) {
            this.transcriptsId = transcriptsId;
            return this;
        }

        public GeneQueryBuilder withTranscriptsName(List<String> transcriptsName) {
            this.transcriptsName = transcriptsName;
            return this;
        }

        public GeneQueryBuilder withTranscriptsAnnotationFlags(LogicalList<String> transcriptsAnnotationFlags) {
            this.transcriptsAnnotationFlags = transcriptsAnnotationFlags;
            return this;
        }

        public GeneQueryBuilder withTranscriptsTfbsId(LogicalList<String> transcriptsTfbsId) {
            this.transcriptsTfbsId = transcriptsTfbsId;
            return this;
        }

        public GeneQueryBuilder withTranscriptsTfbsPfmId(LogicalList<String> transcriptsTfbsPfmId) {
            this.transcriptsTfbsPfmId = transcriptsTfbsPfmId;
            return this;
        }

        public GeneQueryBuilder withTranscriptsTfbsTranscriptionFactors(LogicalList<String> transcriptsTfbsTranscriptionFactors) {
            this.transcriptsTfbsTranscriptionFactors = transcriptsTfbsTranscriptionFactors;
            return this;
        }

        public GeneQueryBuilder withAnnotationOntologiesId(LogicalList<String> annotationOntologiesId) {
            this.annotationOntologiesId = annotationOntologiesId;
            return this;
        }

        public GeneQueryBuilder withAnnotationDiseasesId(LogicalList<String> annotationDiseasesId) {
            this.annotationDiseasesId = annotationDiseasesId;
            return this;
        }

        public GeneQueryBuilder withAnnotationDiseasesName(LogicalList<String> annotationDiseasesName) {
            this.annotationDiseasesName = annotationDiseasesName;
            return this;
        }

        public GeneQueryBuilder withAnnotationExpressionTissue(LogicalList<String> annotationExpressionTissue) {
            this.annotationExpressionTissue = annotationExpressionTissue;
            return this;
        }

        public GeneQueryBuilder withAnnotationExpressionValue(LogicalList<String> annotationExpressionValue) {
            this.annotationExpressionValue = annotationExpressionValue;
            return this;
        }

        public GeneQueryBuilder withAnnotationDrugsName(LogicalList<String> annotationDrugsName) {
            this.annotationDrugsName = annotationDrugsName;
            return this;
        }

        public GeneQueryBuilder withAnnotationConstraintsName(LogicalList<String> annotationConstraintsName) {
            this.annotationConstraintsName = annotationConstraintsName;
            return this;
        }

        public GeneQueryBuilder withAnnotationConstraintsValue(LogicalList<String> annotationConstraintsValue) {
            this.annotationConstraintsValue = annotationConstraintsValue;
            return this;
        }

        public GeneQueryBuilder withMirnas(LogicalList<String> mirnas) {
            this.mirnas = mirnas;
            return this;
        }

        public GeneQueryBuilder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public GeneQueryBuilder withSkip(Integer skip) {
            this.skip = skip;
            return this;
        }

        public GeneQueryBuilder withCount(Boolean count) {
            this.count = count;
            return this;
        }

        public GeneQueryBuilder withSort(String sort) {
            this.sort = sort;
            return this;
        }

        public GeneQueryBuilder withOrder(Order order) {
            this.order = order;
            return this;
        }

        public GeneQueryBuilder withFacet(String facet) {
            this.facet = facet;
            return this;
        }

        public GeneQueryBuilder withIncludes(List<String> includes) {
            this.includes = includes;
            return this;
        }

        public GeneQueryBuilder withExcludes(List<String> excludes) {
            this.excludes = excludes;
            return this;
        }

        public GeneQuery build() {
            GeneQuery geneQuery = new GeneQuery();
            geneQuery.setIds(ids);
            geneQuery.setNames(names);
            geneQuery.setBiotypes(biotypes);
            geneQuery.setSupportLevels(supportLevels);
            geneQuery.setRegions(regions);
            geneQuery.setTranscriptsBiotype(transcriptsBiotype);
            geneQuery.setTranscriptsXrefs(transcriptsXrefs);
            geneQuery.setTranscriptsId(transcriptsId);
            geneQuery.setTranscriptsName(transcriptsName);
            geneQuery.setTranscriptsAnnotationFlags(transcriptsAnnotationFlags);
            geneQuery.setTranscriptsTfbsId(transcriptsTfbsId);
            geneQuery.setTranscriptsTfbsPfmId(transcriptsTfbsPfmId);
            geneQuery.setTranscriptsTfbsTranscriptionFactors(transcriptsTfbsTranscriptionFactors);
            geneQuery.setAnnotationOntologiesId(annotationOntologiesId);
            geneQuery.setAnnotationDiseasesId(annotationDiseasesId);
            geneQuery.setAnnotationDiseasesName(annotationDiseasesName);
            geneQuery.setAnnotationExpressionTissue(annotationExpressionTissue);
            geneQuery.setAnnotationExpressionValue(annotationExpressionValue);
            geneQuery.setAnnotationDrugsName(annotationDrugsName);
            geneQuery.setAnnotationConstraintsName(annotationConstraintsName);
            geneQuery.setAnnotationConstraintsValue(annotationConstraintsValue);
            geneQuery.setMirnas(mirnas);
            geneQuery.setLimit(limit);
            geneQuery.setSkip(skip);
            geneQuery.setCount(count);
            geneQuery.setSort(sort);
            geneQuery.setOrder(order);
            geneQuery.setFacet(facet);
            geneQuery.setIncludes(includes);
            geneQuery.setExcludes(excludes);
            return geneQuery;
        }
    }
}
