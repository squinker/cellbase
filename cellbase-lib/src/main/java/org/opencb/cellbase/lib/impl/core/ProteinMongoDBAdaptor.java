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

package org.opencb.cellbase.lib.impl.core;

import com.mongodb.BasicDBList;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.opencb.biodata.formats.protein.uniprot.v201504jaxb.Entry;
import org.opencb.biodata.models.variant.avro.ProteinFeature;
import org.opencb.biodata.models.variant.avro.ProteinVariantAnnotation;
import org.opencb.biodata.models.variant.avro.Score;
import org.opencb.cellbase.core.api.core.ProteinDBAdaptor;
import org.opencb.cellbase.core.variant.annotation.VariantAnnotationUtils;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.cellbase.core.result.CellBaseDataResult;
import org.opencb.commons.datastore.mongodb.MongoDBCollection;
import org.opencb.commons.datastore.mongodb.MongoDataStore;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by imedina on 01/12/15.
 */
public class ProteinMongoDBAdaptor extends MongoDBAdaptor implements ProteinDBAdaptor<Entry> {

    private MongoDBCollection proteinSubstitutionMongoDBCollection;

    private static final int NUM_PROTEIN_SUBSTITUTION_SCORE_METHODS = 2;

    private static Map<String, String> aaShortNameMap = new HashMap<>();

    static {
        aaShortNameMap.put("ALA", "A");
        aaShortNameMap.put("ARG", "R");
        aaShortNameMap.put("ASN", "N");
        aaShortNameMap.put("ASP", "D");
        aaShortNameMap.put("CYS", "C");
        aaShortNameMap.put("GLN", "Q");
        aaShortNameMap.put("GLU", "E");
        aaShortNameMap.put("GLY", "G");
        aaShortNameMap.put("HIS", "H");
        aaShortNameMap.put("ILE", "I");
        aaShortNameMap.put("LEU", "L");
        aaShortNameMap.put("LYS", "K");
        aaShortNameMap.put("MET", "M");
        aaShortNameMap.put("PHE", "F");
        aaShortNameMap.put("PRO", "P");
        aaShortNameMap.put("SER", "S");
        aaShortNameMap.put("THR", "T");
        aaShortNameMap.put("TRP", "W");
        aaShortNameMap.put("TYR", "Y");
        aaShortNameMap.put("VAL", "V");
    }


    public ProteinMongoDBAdaptor(String species, String assembly, MongoDataStore mongoDataStore) {
        super(species, assembly, mongoDataStore);
        mongoDBCollection = mongoDataStore.getCollection("protein");
        proteinSubstitutionMongoDBCollection = mongoDataStore.getCollection("protein_functional_prediction");

        logger.debug("ProteinMongoDBAdaptor: in 'constructor'");
    }


    @Override
    public CellBaseDataResult<Score> getSubstitutionScores(Query query, QueryOptions options) {
        CellBaseDataResult result = null;

        // Ensembl transcript id is needed for this collection
        if (query.getString("transcript") != null) {
            Bson transcript = Filters.eq("transcriptId", query.getString("transcript"));

            int position = -1;
            String aaShortName = null;
            // If position and aa change are provided we create a 'projection' to return only the required data from the database
            if (query.get("position") != null && !query.getString("position").isEmpty() && query.getInt("position", 0) != 0) {
                position = query.getInt("position");
                String projectionString = "aaPositions." + position;

                // If aa change is provided we only return that information
                if (query.getString("aa") != null && !query.getString("aa").isEmpty()) {
                    aaShortName = aaShortNameMap.get(query.getString("aa").toUpperCase());
                    projectionString += "." + aaShortName;
                }

                // Projection is used to minimize the returned data
                Bson positionProjection = Projections.include(projectionString);
                result = new CellBaseDataResult<>(proteinSubstitutionMongoDBCollection.find(transcript, positionProjection, options));
            } else {
                // Return the whole transcript data
                result = new CellBaseDataResult<>(proteinSubstitutionMongoDBCollection.find(transcript, options));
            }

            if (result != null && !result.getResults().isEmpty()) {
                Document document = (Document) result.getResults().get(0);
                Document aaPositionsDocument = (Document) document.get("aaPositions");

                // Position or aa change were not provided, returning whole transcript data
                if (position == -1 || aaShortName == null) {
                    // Return only the inner Document, not the whole document projected
                    result.setResults(Collections.singletonList(aaPositionsDocument));
                // Position and aa were provided, return only corresponding Score objects
                } else {
                    List<Score> scoreList = null;
                    if (result.getNumResults() == 1 && aaPositionsDocument != null) {
                        scoreList = new ArrayList<>(NUM_PROTEIN_SUBSTITUTION_SCORE_METHODS);
                        Document positionDocument = (Document) aaPositionsDocument.get(Integer.toString(position));
                        Document aaDocument = (Document) positionDocument.get(aaShortName);
                        if (aaDocument.get("ss") != null) {
                            scoreList.add(new Score(Double.parseDouble("" + aaDocument.get("ss")),
                                    "sift", VariantAnnotationUtils.SIFT_DESCRIPTIONS.get(aaDocument.get("se"))));
                        }
                        if (aaDocument.get("ps") != null) {
                            scoreList.add(new Score(Double.parseDouble("" + aaDocument.get("ps")),
                                    "polyphen", VariantAnnotationUtils.POLYPHEN_DESCRIPTIONS.get(aaDocument.get("pe"))));
                        }
                    }
                    result.setResults(scoreList);
                }
            }
        }
        // Return null if no transcript id is provided
        return result;

    }

    @Override
    public CellBaseDataResult<ProteinVariantAnnotation> getVariantAnnotation(String ensemblTranscriptId, int position, String aaReference,
                                                                      String aaAlternate, QueryOptions options) {
        CellBaseDataResult<ProteinVariantAnnotation> cellBaseDataResult = new CellBaseDataResult<>();
        cellBaseDataResult.setId(ensemblTranscriptId + "/" + position + "/" + aaAlternate);
        long dbTimeStart = System.currentTimeMillis();

        ProteinVariantAnnotation proteinVariantAnnotation = new ProteinVariantAnnotation();
        proteinVariantAnnotation.setPosition(position);
        proteinVariantAnnotation.setReference(aaReference);
        proteinVariantAnnotation.setAlternate(aaAlternate);
        Query query = new Query("transcript", ensemblTranscriptId).append("position", position).append("aa", aaAlternate);
        // Stop_gain/lost variants do not have SIFT/POLYPHEN scores
        if (!aaAlternate.equals("STOP") && !aaReference.equals("STOP")) {
            proteinVariantAnnotation.setSubstitutionScores(getSubstitutionScores(query, null).getResults());
        }

        CellBaseDataResult proteinVariantData = null;
        String shortAlternativeAa = aaShortNameMap.get(aaAlternate);
        if (shortAlternativeAa != null) {
            List<Bson> pipeline = new ArrayList<>();
            pipeline.add(new Document("$match", new Document("dbReference.id", ensemblTranscriptId)));

            Document projection = new Document();
            projection.put("accession", 1);
            projection.put("keyword", 1);
            projection.put("feature", 1);
            pipeline.add(new Document("$project", projection));

            pipeline.add(new Document("$unwind", "$feature"));

            BasicDBList andDBList2 = new BasicDBList();
            andDBList2.add(new Document("feature.location.position.position", position));
            andDBList2.add(new Document("feature.variation", shortAlternativeAa));
            Document firstOr = new Document("$and", andDBList2);
            BasicDBList andDBList3 = new BasicDBList();
            andDBList3.add(new Document("feature.location.end.position", new Document("$gte", position)));
            andDBList3.add(new Document("feature.location.begin.position", new Document("$lte", position)));
            Document secondOr = new Document();
            secondOr.put("$and", andDBList3);
            BasicDBList orList = new BasicDBList();
            orList.add(firstOr);
            orList.add(secondOr);
            pipeline.add(new Document("$match", new Document("$or", orList)));

            Document groupFields = new Document();
            groupFields.put("_id", "$accession");
            groupFields.put("keyword", new Document("$addToSet", "$keyword"));
            groupFields.put("feature", new Document("$addToSet", "$feature"));
            pipeline.add(new Document("$group", groupFields));

            proteinVariantData = executeAggregation2(ensemblTranscriptId + "_" + String.valueOf(position) + "_"
                    + aaAlternate, pipeline, new QueryOptions());
            if (proteinVariantData.getNumResults() > 0) {
                proteinVariantAnnotation = processProteinVariantData(proteinVariantAnnotation, shortAlternativeAa,
                        (Document) proteinVariantData.getResults().get(0));
            }
        }

        long dbTimeEnd = System.currentTimeMillis();
        cellBaseDataResult.setTime(Long.valueOf(dbTimeEnd - dbTimeStart).intValue());
        cellBaseDataResult.setNumResults(1);
        cellBaseDataResult.setResults(Collections.singletonList(proteinVariantAnnotation));
        return cellBaseDataResult;
    }

    @Override
    public CellBaseDataResult rank(Query query, String field, int numResults, boolean asc) {
        return null;
    }

    @Override
    public CellBaseDataResult groupBy(Query query, String field, QueryOptions options) {
        Bson bsonQuery = parseQuery(query);
        return groupBy(bsonQuery, field, "name", options);
    }

    @Override
    public CellBaseDataResult groupBy(Query query, List<String> fields, QueryOptions options) {
        Bson bsonQuery = parseQuery(query);
        return groupBy(bsonQuery, fields, "name", options);
    }

    @Override
    public CellBaseDataResult<Long> update(List objectList, String field, String[] innerFields) {
        return null;
    }

    @Override
    public CellBaseDataResult<Long> count(Query query) {
        Bson document = parseQuery(query);
        return new CellBaseDataResult<>(mongoDBCollection.count(document));
    }

    @Override
    public CellBaseDataResult distinct(Query query, String field) {
        Bson document = parseQuery(query);
        return new CellBaseDataResult<>(mongoDBCollection.distinct(field, document));
    }

    @Override
    public CellBaseDataResult stats(Query query) {
        return null;
    }

    @Override
    public CellBaseDataResult<Entry> get(Query query, QueryOptions options) {
        Bson bson = parseQuery(query);
        return new CellBaseDataResult<>(mongoDBCollection.find(bson, null, Entry.class, options));
    }

    @Override
    public CellBaseDataResult nativeGet(Query query, QueryOptions options) {
        Bson bson = parseQuery(query);
        return new CellBaseDataResult<>(mongoDBCollection.find(bson, options));
    }

    @Override
    public Iterator<Entry> iterator(Query query, QueryOptions options) {
        return null;
    }

    @Override
    public Iterator nativeIterator(Query query, QueryOptions options) {
        Bson bson = parseQuery(query);
        return mongoDBCollection.nativeQuery().find(bson, options);
    }

    @Override
    public void forEach(Query query, Consumer<? super Object> action, QueryOptions options) {

    }

    private Bson parseQuery(Query query) {
        List<Bson> andBsonList = new ArrayList<>();

        createOrQuery(query, QueryParams.ACCESSION.key(), "accession", andBsonList);
        createOrQuery(query, QueryParams.NAME.key(), "name", andBsonList);
        createOrQuery(query, QueryParams.GENE.key(), "gene.name.value", andBsonList);
        createOrQuery(query, QueryParams.XREFS.key(), "dbReference.id", andBsonList);
        createOrQuery(query, QueryParams.KEYWORD.key(), "keyword.value", andBsonList);
        createOrQuery(query, QueryParams.FEATURE_ID.key(), "feature.id", andBsonList);
        createOrQuery(query, QueryParams.FEATURE_TYPE.key(), "feature.type", andBsonList);

        if (andBsonList.size() > 0) {
            return Filters.and(andBsonList);
        } else {
            return new Document();
        }
    }

    private ProteinVariantAnnotation processProteinVariantData(ProteinVariantAnnotation proteinVariantAnnotation,
                                                               String shortAlternativeAa, Document proteinVariantData) {

        proteinVariantAnnotation.setUniprotAccession(proteinVariantData.get("_id", ArrayList.class).get(0).toString());

        proteinVariantAnnotation.setKeywords(new ArrayList<>());
        ArrayList keywordList = (ArrayList) proteinVariantData.get("keyword", ArrayList.class).get(0);
        for (Object keywordObject : keywordList) {
            proteinVariantAnnotation.getKeywords().add((String) ((Document) keywordObject).get("value"));
        }

        proteinVariantAnnotation.setFeatures(new ArrayList<>());
        ArrayList featureList = proteinVariantData.get("feature", ArrayList.class);
        for (Object featureObject : featureList) {
            Document featureDocument = (Document) featureObject;
            String type = (String) featureDocument.get("type");


            ArrayList variationList = featureDocument.get("variation", ArrayList.class);
            //Check and process protein variants within the "feature" list
//            if(type!=null && type.equals("sequence variant") &&
//                    ((int)((Document)((Document) featureDBObject.get("location")).get("position"))
//                            .get("position"))==proteinVariantAnnotation.getPosition()) {
            // Current feature corresponds to current variant
            if (variationList != null && variationList.contains(shortAlternativeAa)) {
                proteinVariantAnnotation.setUniprotVariantId((String) featureDocument.get("id"));
                proteinVariantAnnotation.setFunctionalDescription((String) featureDocument.get("description"));
                // Not a protein variant, another type of feature e.g. protein domain
            } else {
                ProteinFeature proteinFeature = new ProteinFeature();
                proteinFeature.setId((String) featureDocument.get("id"));
                proteinFeature.setType((String) featureDocument.get("type"));
                proteinFeature.setDescription((String) featureDocument.get("description"));
//                proteinFeature.setRef((String) featureDBObject.get("ref"));
                if (featureDocument.get("location") != null) {
                    if (((Document) featureDocument.get("location")).get("begin") != null) {
                        proteinFeature.setStart((int) ((Document) ((Document) featureDocument.get("location"))
                                .get("begin")).get("position"));
                        if (((Document) featureDocument.get("location")).get("end") != null) {
                            proteinFeature.setEnd((int) ((Document) ((Document) featureDocument.get("location"))
                                    .get("end")).get("position"));
                        } else {
                            proteinFeature.setEnd(proteinFeature.getStart());
                        }
                    } else if (((Document) featureDocument.get("location")).get("position") != null) {
                        proteinFeature.setStart((int) ((Document) ((Document) featureDocument.get("location"))
                                .get("position")).get("position"));
                        proteinFeature.setEnd(proteinFeature.getStart());
                    }
                }
                proteinVariantAnnotation.getFeatures().add(proteinFeature);
            }
        }
        return proteinVariantAnnotation;
    }
}