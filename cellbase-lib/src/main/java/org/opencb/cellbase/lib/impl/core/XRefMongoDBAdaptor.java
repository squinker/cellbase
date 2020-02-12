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

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.opencb.biodata.models.core.Xref;
import org.opencb.cellbase.core.api.core.XRefDBAdaptor;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.cellbase.core.result.CellBaseDataResult;
import org.opencb.commons.datastore.mongodb.MongoDataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Created by imedina on 07/12/15.
 */
public class XRefMongoDBAdaptor extends MongoDBAdaptor implements XRefDBAdaptor<Xref> {

    public XRefMongoDBAdaptor(String species, String assembly, MongoDataStore mongoDataStore) {
        super(species, assembly, mongoDataStore);
        mongoDBCollection = mongoDataStore.getCollection("gene");

        logger.debug("XRefMongoDBAdaptor: in 'constructor'");
    }


    @Override
    public CellBaseDataResult startsWith(String id, QueryOptions options) {
        Bson regex = Filters.regex("transcripts.xrefs.id", Pattern.compile("^" + id));
        Bson include = Projections.include("id", "name", "chromosome", "start", "end");
        return new CellBaseDataResult(mongoDBCollection.find(regex, include, options));
    }

    @Override
    public CellBaseDataResult contains(String id, QueryOptions options) {
        Bson regex = Filters.regex("transcripts.xrefs.id", Pattern.compile("\\w*" + id + "\\w*"));
        Bson include = Projections.include("id", "name", "chromosome", "start", "end");
        return new CellBaseDataResult(mongoDBCollection.find(regex, include, options));
    }

    @Override
    public CellBaseDataResult<Long> update(List objectList, String field, String[] innerFields) {
        return null;
    }

    @Override
    public CellBaseDataResult<Long> count(Query query) {
        Bson bson = parseQuery(query);
        return new CellBaseDataResult(mongoDBCollection.count(bson));
    }

    @Override
    public CellBaseDataResult distinct(Query query, String field) {
        Bson bson = parseQuery(query);
        return new CellBaseDataResult(mongoDBCollection.distinct(field, bson));
    }

    @Override
    public CellBaseDataResult stats(Query query) {
        return null;
    }

    @Override
    public CellBaseDataResult<Xref> get(Query query, QueryOptions options) {
        return null;
    }

    @Override
    public CellBaseDataResult nativeGet(Query query, QueryOptions options) {
        Bson bson = parseQuery(query);
        Bson match = Aggregates.match(bson);

        Bson project = Aggregates.project(Projections.include("transcripts.xrefs"));
        Bson unwind = Aggregates.unwind("$transcripts");
        Bson unwind2 = Aggregates.unwind("$transcripts.xrefs");

        // This project the three fields of Xref to the top of the object
        Document document = new Document("id", "$transcripts.xrefs.id");
        document.put("dbName", "$transcripts.xrefs.dbName");
        document.put("dbDisplayName", "$transcripts.xrefs.dbDisplayName");
        Bson project1 = Aggregates.project(document);

        if (query.containsKey(QueryParams.DBNAME.key())) {
            Bson bson2 = parseQuery(new Query(QueryParams.DBNAME.key(), query.get(QueryParams.DBNAME.key())));
            Bson match2 = Aggregates.match(bson2);
            return new CellBaseDataResult(mongoDBCollection.aggregate(
                    Arrays.asList(match, project, unwind, unwind2, match2, project1), options));
        }
        return new CellBaseDataResult(mongoDBCollection.aggregate(Arrays.asList(match, project, unwind, unwind2, project1), options));
    }

    @Override
    public Iterator<Xref> iterator(Query query, QueryOptions options) {
        return null;
    }

    @Override
    public Iterator nativeIterator(Query query, QueryOptions options) {
        Bson bson = parseQuery(query);
        return mongoDBCollection.nativeQuery().find(bson, options);
    }

    @Override
    public CellBaseDataResult rank(Query query, String field, int numResults, boolean asc) {
        return null;
    }

    @Override
    public CellBaseDataResult groupBy(Query query, String field, QueryOptions options) {
        return groupBy(parseQuery(query), field, "name", options);
    }

    @Override
    public CellBaseDataResult groupBy(Query query, List<String> fields, QueryOptions options) {
        return groupBy(parseQuery(query), fields, "name", options);
    }

    @Override
    public void forEach(Query query, Consumer<? super Object> action, QueryOptions options) {

    }

    private Bson parseQuery(Query query) {
        List<Bson> andBsonList = new ArrayList<>();
        createOrQuery(query, XRefDBAdaptor.QueryParams.ID.key(), "transcripts.xrefs.id", andBsonList);
        createOrQuery(query, XRefDBAdaptor.QueryParams.DBNAME.key(), "transcripts.xrefs.dbName", andBsonList);

        if (andBsonList.size() > 0) {
            return Filters.and(andBsonList);
        } else {
            return new Document();
        }
    }

}