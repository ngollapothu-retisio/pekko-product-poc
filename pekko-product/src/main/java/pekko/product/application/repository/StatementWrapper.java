package pekko.product.application.repository;

import io.r2dbc.spi.Statement;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class StatementWrapper {
    private Statement stmt;
    public StatementWrapper(Statement stmt){
        this.stmt = stmt;
    }
    public StatementWrapper bind(int index, Object object, Class<?> type){
        return Optional.ofNullable(object)
                .map(o -> {
                    this.stmt.bind(index, object);
                    return this;
                })
                .orElseGet(()->{
                    this.stmt.bindNull(index, type);
                    return this;
                });
    }
    public StatementWrapper bindString(int index, Object object){
        return this.bind(index, object, String.class);
    }
    public StatementWrapper bindBoolean(int index, Object object){
        return this.bind(index, object, Boolean.class);
    }
    public StatementWrapper bindInteger(int index, Object object){
        return this.bind(index, object, Integer.class);
    }
    public StatementWrapper bindLong(int index, Object object){
        return this.bind(index, object, Long.class);
    }
    public StatementWrapper bindDouble(int index, Object object){
        return this.bind(index, object, Double.class);
    }
    public StatementWrapper bindFloat(int index, Object object){
        return this.bind(index, object, Float.class);
    }
    public StatementWrapper bindTimestamp(int index, Object object){
        return this.bind(index, object, Timestamp.class);
    }
    public StatementWrapper bindLocalDateTime(int index, Object object){
        return this.bind(index, object, LocalDateTime.class);
    }
    public Statement getStatement(){
        return this.stmt;
    }
}
//$1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20