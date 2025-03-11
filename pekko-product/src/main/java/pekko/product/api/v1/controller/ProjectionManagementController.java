package pekko.product.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.persistence.query.Offset;
import org.apache.pekko.persistence.query.TimestampOffset;
import org.apache.pekko.projection.ProjectionId;
import org.apache.pekko.projection.javadsl.ProjectionManagement;
import pekko.product.api.v1.response.BaseResponse;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Slf4j
public class ProjectionManagementController extends Controller {

    private final ProjectionManagement projectionManagement;
    
    @Inject
    public ProjectionManagementController(ActorSystem classicActorSystem){
        org.apache.pekko.actor.typed.ActorSystem<Void> typedActorSystem = Adapter.toTyped(classicActorSystem);
        
        // Projection Management
        this.projectionManagement = ProjectionManagement.get(typedActorSystem);

    }

    private Result constructResult(BaseResponse r) {
        return Optional.ofNullable(r.getStatusCode()).map(t -> status(r.getStatusCode(), Json.toJson(r))).orElseGet(()->ok(Json.toJson(r)));
    }
    
    // Get status of a projection
    public CompletionStage<Result> status(String name, String key) {
        ProjectionId projectionId = ProjectionId.of(name, key);
        return projectionManagement.isPaused(projectionId)
                .thenCombine(projectionManagement.getOffset(projectionId), (isPaused, offset) -> {
                    log.info("name:{}, key:{}, isPaused:{}, offset:{}", name, key, isPaused, offset.get());
                    return new BaseResponse(200, "name:"+name+", key:"+key+", isPaused:"+isPaused+", offset:"+offset.get());
                })
                .exceptionally(ex -> {
                    log.info("name:{}, key:{}, error:{}", name, key, ex.getMessage());
                    return new BaseResponse(400, "name:"+name+", key:"+key+", error:"+ex.getMessage());
                })
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> pause(String name, String key) {
        ProjectionId projectionId = ProjectionId.of(name, key);
        return projectionManagement.pause(projectionId)
                .thenCompose(done -> status(name, key));
    }
    public CompletionStage<Result> resume(String name, String key) {
        ProjectionId projectionId = ProjectionId.of(name, key);
        return projectionManagement.resume(projectionId)
                .thenCompose(done -> status(name, key));
    }
    //clearOffset & updateOffset - NOT STABLE
/*    public CompletionStage<Result> clearOffset(String name, String key) {
        ProjectionId projectionId = ProjectionId.of(name, key);
        return projectionManagement.clearOffset(projectionId)
                .thenCompose(done -> status(name, key));
    }
    public CompletionStage<Result> updateOffset(String name, String key, String sequence) {
        ProjectionId projectionId = ProjectionId.of(name, key);

        return projectionManagement.getOffset(projectionId)
                .thenApply(o -> {
                    if(o.isPresent()){
                        return projectionManagement.updateOffset(projectionId, Offset.sequence(Integer.valueOf(sequence)));
                    }
                    return Done.done();
                })
                .thenCompose(done -> status(name, key));
    }*/
}
