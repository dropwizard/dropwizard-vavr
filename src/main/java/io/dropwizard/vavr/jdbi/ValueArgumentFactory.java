package io.dropwizard.vavr.jdbi;

import io.vavr.Value;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ValueArgumentFactory implements ArgumentFactory<Value<?>> {
    private static class DefaultValueArgument implements Argument {
        private final Value<?> value;
        private final int nullType;

        private DefaultValueArgument(Value<?> value, int nullType) {
            this.value = value;
            this.nullType = nullType;
        }

        private DefaultValueArgument(Value<?> value) {
            this(value, Types.OTHER);
        }

        @Override
        public void apply(int position,
                          PreparedStatement statement,
                          StatementContext ctx) throws SQLException {
            if (value.isEmpty()) {
                statement.setNull(position, nullType);
            } else {
                statement.setObject(position, value.get());
            }
        }
    }

    private static class MsSqlValueArgument implements Argument {
        private final Value<?> value;

        private MsSqlValueArgument(Value<?> value) {
            this.value = value;
        }

        @Override
        public void apply(int position,
                          PreparedStatement statement,
                          StatementContext ctx) throws SQLException {
            statement.setObject(position, value.getOrElse(() -> null));
        }
    }

    private final String jdbcDriver;

    public ValueArgumentFactory(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof Value;
    }

    @Override
    public Argument build(Class<?> expectedType, Value<?> value, StatementContext ctx) {
        if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(jdbcDriver)) {
            return new MsSqlValueArgument(value);
        } else if ("oracle.jdbc.OracleDriver".equals(jdbcDriver)) {
            return new DefaultValueArgument(value, Types.NULL);
        }
        return new DefaultValueArgument(value);
    }
}
