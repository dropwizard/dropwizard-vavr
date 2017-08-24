package io.dropwizard.vavr.jdbi;

import io.vavr.control.Option;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class OptionArgumentFactory implements ArgumentFactory<Option<?>> {
    private static class DefaultOptionArgument implements Argument {
        private final Option<?> value;
        private final int nullType;

        private DefaultOptionArgument(Option<?> value, int nullType) {
            this.value = value;
            this.nullType = nullType;
        }

        private DefaultOptionArgument(Option<?> value) {
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

    private static class MsSqlOptionArgument implements Argument {
        private final Option<?> value;

        private MsSqlOptionArgument(Option<?> value) {
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

    public OptionArgumentFactory(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof Option;
    }

    @Override
    public Argument build(Class<?> expectedType, Option<?> value, StatementContext ctx) {
        if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(jdbcDriver)) {
            return new MsSqlOptionArgument(value);
        } else if ("oracle.jdbc.OracleDriver".equals(jdbcDriver)) {
            return new DefaultOptionArgument(value, Types.NULL);
        }
        return new DefaultOptionArgument(value);
    }
}
