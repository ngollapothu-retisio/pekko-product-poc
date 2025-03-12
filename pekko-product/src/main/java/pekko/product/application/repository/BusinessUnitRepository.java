package pekko.product.application.repository;


import io.r2dbc.spi.Row;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;
import pekko.product.application.domain.BusinessUnit;

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
public class BusinessUnitRepository {

    private final org.apache.pekko.actor.typed.ActorSystem typedActorSystem;

    @Inject
    public BusinessUnitRepository(ActorSystem classicActorSystem) {
        typedActorSystem = Adapter.toTyped(classicActorSystem);
    }

    String SAVE_BUSINESS_UNIT = "INSERT INTO POC_BUSINESS_UNIT(" +
            "BUSINESS_UNIT_ID," +
            "BUSINESS_UNIT_NAME," +
            "ACTIVE, " +
            "DELETED, " +
            "LAST_MODIFIED_TMST) " +
            " VALUES ($1, $2, $3, $4, $5)" +
            " ON CONFLICT (BUSINESS_UNIT_ID) " +
            "DO UPDATE SET " +
            "BUSINESS_UNIT_ID = EXCLUDED.BUSINESS_UNIT_ID," +
            "BUSINESS_UNIT_NAME = EXCLUDED.BUSINESS_UNIT_NAME," +
            "ACTIVE = EXCLUDED.ACTIVE," +
            "DELETED = EXCLUDED.DELETED," +
            "LAST_MODIFIED_TMST=excluded.LAST_MODIFIED_TMST ";

    public CompletionStage<Done> saveBusinessUnit(BusinessUnit businessUnit) {
        return R2dbcRepository.withProjectionSession(typedActorSystem, 
                r2dbcSession -> saveBusinessUnit(r2dbcSession, businessUnit)
        );
    }
    public CompletionStage<Done> saveBusinessUnit(R2dbcSession r2dbcSession, BusinessUnit businessUnit) {
        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(SAVE_BUSINESS_UNIT));
        statementWrapper.bind(index.incrementAndGet(), businessUnit.id(), String.class);
        statementWrapper.bind(index.incrementAndGet(), businessUnit.name(), String.class);
        statementWrapper.bind(index.incrementAndGet(), businessUnit.active(), Boolean.class);
        statementWrapper.bind(index.incrementAndGet(), businessUnit.deleted(), Boolean.class);
        statementWrapper.bind(index.incrementAndGet(), Timestamp.valueOf(LocalDateTime.now()), Timestamp.class);
        return r2dbcSession.updateOne(statementWrapper.getStatement())
                .handle((result, ex) -> {
                    if(ex == null){
                        return result;
                    }
                    log.error("Exception in saveBusinessUnit, id::{}", businessUnit.id(), ex);
                    throw new RuntimeException(ex);
                })
                .thenApply(r -> Done.getInstance());
    }
    public CompletionStage<Done> patchBusinessUnit(BusinessUnit businessUnit, String id) {
        return R2dbcRepository.withProjectionSession(typedActorSystem,
                r2dbcSession -> patchBusinessUnit(r2dbcSession, businessUnit, id)
        );
    }
    public CompletionStage<Done> patchBusinessUnit(R2dbcSession r2dbcSession, BusinessUnit businessUnit, String id) {
        AtomicInteger qIndex = new AtomicInteger();
        StringBuilder patchQuery = new StringBuilder("UPDATE POC_BUSINESS_UNIT SET ");
        if(Objects.nonNull(businessUnit.name())){
            patchQuery.append("BUSINESS_UNIT_NAME = $"+qIndex.incrementAndGet()+", ");
        }
        if (businessUnit.active() != null) {
            patchQuery.append( "ACTIVE = $" + qIndex.incrementAndGet() + ", ");
        }
        if (businessUnit.deleted() != null) {
            patchQuery.append( "DELETED = $" + qIndex.incrementAndGet() + ", ");
        }
        patchQuery.append("LAST_MODIFIED_TMST = now() WHERE BUSINESS_UNIT_ID = $"+qIndex.incrementAndGet()+"");
        String query = patchQuery.toString();
        log.info("query::{}", query);

        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(query));
        if(Objects.nonNull(businessUnit.name())){
            statementWrapper.bind(index.incrementAndGet(), businessUnit.name(), String.class);
        }
        if (businessUnit.active() != null) {
            statementWrapper.bind(index.incrementAndGet(), businessUnit.active(), Boolean.class);
        }
        if (businessUnit.deleted() != null) {
            statementWrapper.bind(index.incrementAndGet(), businessUnit.deleted(), Boolean.class);
        }

        statementWrapper.bind(index.incrementAndGet(), id, String.class);

        return r2dbcSession.updateOne(statementWrapper.getStatement())
                .handle((result, ex) -> {
                    if (ex == null) {
                        return result;
                    }
                    log.error("Exception in patchBusinessUnit, id::{}", id, ex);
                    throw new RuntimeException(ex);
                })
                .thenApply(r -> Done.getInstance());
    }
    

    private final String DELETE_BUSINESS_UNIT = "DELETE" +
            " FROM POC_BUSINESS_UNIT" +
            " WHERE BUSINESS_UNIT_ID = $1";

    public CompletionStage<Done> deleteBusinessUnit(String id) {
        return R2dbcRepository.withProjectionSession(typedActorSystem, 
                r2dbcSession -> deleteBusinessUnit(r2dbcSession, id)
        );
    }

    private CompletionStage<Done> deleteBusinessUnit(R2dbcSession r2dbcSession, String id) {
        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(DELETE_BUSINESS_UNIT));
        statementWrapper.bind(index.incrementAndGet(), id, String.class);
        return r2dbcSession.updateOne(statementWrapper.getStatement())
                .handle((result, ex) -> {
                    if (ex == null) {
                        return result;
                    }
                    log.error("Exception in deleteBusinessUnit, id::{}", id, ex);
                    throw new RuntimeException(ex);
                })
                .thenApply(r -> Done.getInstance());
    }

    private final String GET_BUSINESS_UNIT = "SELECT" +
            " BUSINESS_UNIT_ID," +
            " BUSINESS_UNIT_NAME," +
            " ACTIVE," +
            " DELETED" +
            " FROM POC_BUSINESS_UNIT" +
            " WHERE BUSINESS_UNIT_ID = $1";

    public CompletionStage<Optional<BusinessUnit>> getBusinessUnit(String id) {
        return R2dbcRepository.withProjectionSession(
                typedActorSystem,
                r2dbcSession -> getBusinessUnit(r2dbcSession, id)
        );
    }
    public CompletionStage<Optional<BusinessUnit>> getBusinessUnit(R2dbcSession r2dbcSession, String id) {
        AtomicInteger index = new AtomicInteger(-1);
        StatementWrapper statementWrapper = new StatementWrapper(r2dbcSession.createStatement(GET_BUSINESS_UNIT));
        statementWrapper.bind(index.incrementAndGet(), id, String.class);
        return r2dbcSession.selectOne(
                statementWrapper.getStatement(),
                row -> rowToDomain(row)
        ).handle((result, ex) -> {
            if (ex == null) {
                return result;
            }
            log.error("Exception in getBusinessUnit, id::{}", id, ex);
            return Optional.empty();
        });
    }

    private BusinessUnit rowToDomain(Row row) {
        return new BusinessUnit(
                row.get("BUSINESS_UNIT_ID", String.class),
                row.get("BUSINESS_UNIT_NAME", String.class),
                row.get("ACTIVE", Boolean.class),
                row.get("DELETED", Boolean.class)
        );
    }

}
