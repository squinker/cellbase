package org.opencb.cellbase.grpc.server;

import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.opencb.biodata.models.common.protobuf.service.ServiceTypesModel;
import org.opencb.biodata.models.core.protobuf.GeneModel;
import org.opencb.cellbase.core.api.GeneDBAdaptor;
import org.opencb.cellbase.grpc.service.GeneServiceGrpc;
import org.opencb.cellbase.grpc.service.GenericServiceModel;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.core.QueryResult;

import java.util.Iterator;
import java.util.List;

/**
 * Created by swaathi on 16/12/15.
 */
public class GeneGrpcServer extends GenericGrpcServer implements GeneServiceGrpc.GeneService {


    @Override
    public void count(GenericServiceModel.Request request, StreamObserver<ServiceTypesModel.LongResponse> responseObserver) {
        GeneDBAdaptor geneDBAdaptor = dbAdaptorFactory.getGeneDBAdaptor(request.getSpecies(), request.getAssembly());

        Query query = createQuery(request);
        QueryResult queryResult = geneDBAdaptor.count(query);
        Long value = Long.valueOf(queryResult.getResult().get(0).toString());
        ServiceTypesModel.LongResponse count = ServiceTypesModel.LongResponse.newBuilder()
                .setValue(value)
                .build();
        responseObserver.onNext(count);
        responseObserver.onCompleted();
    }

    @Override
    public void distinct(GenericServiceModel.Request request,
                         StreamObserver<ServiceTypesModel.StringArrayResponse> responseObserver) {
        GeneDBAdaptor geneDBAdaptor = dbAdaptorFactory.getGeneDBAdaptor(request.getSpecies(), request.getAssembly());

        Query query = createQuery(request);
        QueryResult queryResult = geneDBAdaptor.distinct(query, request.getOptions().get("distinct"));
        List values = queryResult.getResult();
        ServiceTypesModel.StringArrayResponse distinctValues = ServiceTypesModel.StringArrayResponse.newBuilder()
                .addAllValues(values)
                .build();
        responseObserver.onNext(distinctValues);
        responseObserver.onCompleted();
    }

    @Override
    public void first(GenericServiceModel.Request request, StreamObserver<GeneModel.Gene> responseObserver) {
        GeneDBAdaptor geneDBAdaptor = dbAdaptorFactory.getGeneDBAdaptor(request.getSpecies(), request.getAssembly());

        QueryOptions queryOptions = createQueryOptions(request);
        QueryResult first = geneDBAdaptor.first(queryOptions);
        responseObserver.onNext(convert((Document) first.getResult().get(0)));
        responseObserver.onCompleted();
    }

    @Override
    public void next(GenericServiceModel.Request request, StreamObserver<GeneModel.Gene> responseObserver) {

    }

    @Override
    public void get(GenericServiceModel.Request request, StreamObserver<GeneModel.Gene> responseObserver) {
        GeneDBAdaptor geneDBAdaptor = dbAdaptorFactory.getGeneDBAdaptor(request.getSpecies(), request.getAssembly());

        Query query = createQuery(request);
        QueryOptions queryOptions = createQueryOptions(request);
        Iterator iterator = geneDBAdaptor.nativeIterator(query, queryOptions);
        while (iterator.hasNext()) {
            Document document = (Document) iterator.next();
            responseObserver.onNext(convert(document));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void groupBy(GenericServiceModel.Request request, StreamObserver<ServiceTypesModel.GroupResponse> responseObserver) {

    }

    private GeneModel.Gene convert(Document document) {
        GeneModel.Gene.Builder builder = GeneModel.Gene.newBuilder()
                .setId((String) document.getOrDefault("id", ""))
                .setName((String) document.getOrDefault("name", ""))
                .setChromosome((String) document.getOrDefault("chromosome", ""))
                .setStart(document.getInteger("start"))
                .setEnd(document.getInteger("end"))
                .setBiotype((String) document.getOrDefault("biotype", ""))
                .setStatus((String) document.getOrDefault("status", ""))
                .setStrand((String) document.getOrDefault("strand", ""))
                .setSource((String) document.getOrDefault("source", ""));
//                .addAllTranscripts()

        return builder.build();
    }
}
