package pekko.product.application.repository;


import io.r2dbc.spi.Row;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;
import pekko.product.application.domain.Product;
import pekko.product.application.event.ProductEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Singleton
public class ProductRepository {

    private final org.apache.pekko.actor.typed.ActorSystem typedActorSystem;

    @Inject
    public ProductRepository(ActorSystem classicActorSystem) {
        typedActorSystem = Adapter.toTyped(classicActorSystem);
    }

    String SAVE_PRODUCT = "INSERT INTO POC_PRODUCT(" +
            "PRODUCT_ID," +
            "PRODUCT_NAME," +
            "ACTIVE, " +
            "DELETED, " +
            "LAST_MODIFIED_TMST) " +
            " VALUES ($1, $2, $3, $4, $5)" +
            " ON CONFLICT (PRODUCT_ID) " +
            "DO UPDATE SET " +
            "PRODUCT_ID = EXCLUDED.PRODUCT_ID," +
            "PRODUCT_NAME = EXCLUDED.PRODUCT_NAME," +
            "ACTIVE = EXCLUDED.ACTIVE," +
            "DELETED = EXCLUDED.DELETED," +
            "LAST_MODIFIED_TMST=excluded.LAST_MODIFIED_TMST ";

    public CompletionStage<Done> saveProduct(Product product) {
        return R2dbcRepository.withProjectionSession(typedActorSystem,
                r2dbcSession -> saveProduct(r2dbcSession, product)
        );
    }
    public CompletionStage<Done> saveProduct(R2dbcSession r2dbcSession, Product product) {
        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(SAVE_PRODUCT));
        statementWrapper.bind(index.incrementAndGet(), product.id(), String.class);
        statementWrapper.bind(index.incrementAndGet(), product.name(), String.class);
        statementWrapper.bind(index.incrementAndGet(), product.active(), Boolean.class);
        statementWrapper.bind(index.incrementAndGet(), product.deleted(), Boolean.class);
        statementWrapper.bind(index.incrementAndGet(), Timestamp.valueOf(LocalDateTime.now()), Timestamp.class);
        return r2dbcSession.updateOne(statementWrapper.getStatement())
                .handle((result, ex) -> {
                    if(ex == null){
                        return result;
                    }
                    log.error("Exception in saveProduct, id::{}", product.id(), ex);
                    throw new RuntimeException(ex);
                })
                .thenApply(r -> Done.getInstance());
    }
    public CompletionStage<Done> patchProduct(ProductEvent.Patched event, String id) {
        return R2dbcRepository.withProjectionSession(typedActorSystem,
                r2dbcSession -> patchProduct(r2dbcSession, event, id)
        );
    }
    public CompletionStage<Done> patchProduct(R2dbcSession r2dbcSession, ProductEvent.Patched event, String id) {
        AtomicInteger qIndex = new AtomicInteger();
        StringBuilder patchQuery = new StringBuilder("UPDATE POC_PRODUCT SET ");
        if(Objects.nonNull(event.name())){
            patchQuery.append("PRODUCT_NAME = $"+qIndex.incrementAndGet()+", ");
        }
        if (event.active() != null) {
            patchQuery.append( "ACTIVE = $" + qIndex.incrementAndGet() + ", ");
        }
        patchQuery.append("LAST_MODIFIED_TMST = now() WHERE PRODUCT_ID = $"+qIndex.incrementAndGet()+"");
        String query = patchQuery.toString();
        log.info("query::{}", query);

        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(query));
        if(Objects.nonNull(event.name())){
            statementWrapper.bind(index.incrementAndGet(), event.name(), String.class);
        }
        if (event.active() != null) {
            statementWrapper.bind(index.incrementAndGet(), event.active(), Boolean.class);
        }
        statementWrapper.bind(index.incrementAndGet(), id, String.class);

        return r2dbcSession.updateOne(statementWrapper.getStatement())
                .handle((result, ex) -> {
                    if (ex == null) {
                        return result;
                    }
                    log.error("Exception in patchProduct, id::{}", id, ex);
                    throw new RuntimeException(ex);
                })
                .thenApply(r -> Done.getInstance());
    }


    private final String DELETE_PRODUCT = "DELETE" +
            " FROM POC_PRODUCT" +
            " WHERE PRODUCT_ID = $1";

    public CompletionStage<Done> deleteProduct(String id) {
        return R2dbcRepository.withProjectionSession(typedActorSystem,
                r2dbcSession -> deleteProduct(r2dbcSession, id)
        );
    }

    private CompletionStage<Done> deleteProduct(R2dbcSession r2dbcSession, String id) {
        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(DELETE_PRODUCT));
        statementWrapper.bind(index.incrementAndGet(), id, String.class);
        return r2dbcSession.updateOne(statementWrapper.getStatement())
                .handle((result, ex) -> {
                    if (ex == null) {
                        return result;
                    }
                    log.error("Exception in deleteProduct, id::{}", id, ex);
                    throw new RuntimeException(ex);
                })
                .thenApply(r -> Done.getInstance());
    }

    private final String GET_PRODUCT = "SELECT" +
            " PRODUCT_ID," +
            " PRODUCT_NAME," +
            " ACTIVE," +
            " DELETED" +
            " FROM POC_PRODUCT" +
            " WHERE PRODUCT_ID = $1";

    public CompletionStage<Optional<Product>> getProduct(String id) {
        return R2dbcRepository.withProjectionSession(
                typedActorSystem,
                r2dbcSession -> getProduct(r2dbcSession, id)
        );
    }
    public CompletionStage<Optional<Product>> getProduct(R2dbcSession r2dbcSession, String id) {
        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(GET_PRODUCT));
        statementWrapper.bind(index.incrementAndGet(), id, String.class);
        return r2dbcSession.selectOne(
                statementWrapper.getStatement(),
                row -> rowToDomain(row)
        ).handle((result, ex) -> {
            if (ex == null) {
                return result;
            }
            log.error("Exception in getProduct, id::{}", id, ex);
            return Optional.empty();
        });
    }

    private Product rowToDomain(Row row) {
        return new Product(
                row.get("PRODUCT_ID", String.class),
                row.get("PRODUCT_NAME", String.class),
                row.get("ACTIVE", Boolean.class),
                row.get("DELETED", Boolean.class)
        );
    }

}
