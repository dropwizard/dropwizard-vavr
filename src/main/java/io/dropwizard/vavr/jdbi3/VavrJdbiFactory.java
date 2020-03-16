package io.dropwizard.vavr.jdbi3;

import com.codahale.metrics.jdbi3.strategies.StatementNameStrategy;
import io.dropwizard.jdbi3.JdbiFactory;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.vavr.VavrPlugin;

public class VavrJdbiFactory extends JdbiFactory {
    public VavrJdbiFactory() {
        super();
    }

    public VavrJdbiFactory(StatementNameStrategy nameStrategy) {
        super(nameStrategy);
    }

    @Override
    protected void configure(Jdbi jdbi) {
        super.configure(jdbi);
        jdbi.installPlugin(new VavrPlugin());
    }
}
