package pekko.product.api;

import lombok.extern.slf4j.Slf4j;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class PingApi  extends Controller {
    public CompletionStage<Result> ping() {
        log.info("ping");
        return CompletableFuture.completedFuture(ok("Server is up ..."));
    }
}
