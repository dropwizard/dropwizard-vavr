package io.dropwizard.vavr.jdbi;

import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class VavrDBIFactory extends DBIFactory {
    @Override
    public DBI build(Environment environment, PooledDataSourceFactory configuration, ManagedDataSource dataSource, String name) {
        final DBI dbi = super.build(environment, configuration, dataSource, name);

        dbi.registerArgumentFactory(new ValueArgumentFactory(configuration.getDriverClass()));
        dbi.registerArgumentFactory(new OptionArgumentFactory(configuration.getDriverClass()));
        dbi.registerContainerFactory(new OptionContainerFactory());

        // The order of container factories is important, least specific to most specific
        dbi.registerContainerFactory(new SeqContainerFactory());
        dbi.registerContainerFactory(new IndexedSeqContainerFactory());
        dbi.registerContainerFactory(new SetContainerFactory());
        dbi.registerContainerFactory(new TreeContainerFactory());
        dbi.registerContainerFactory(new ListContainerFactory());
        dbi.registerContainerFactory(new ArrayContainerFactory());
        dbi.registerContainerFactory(new QueueContainerFactory());
        dbi.registerContainerFactory(new StreamContainerFactory());
        dbi.registerContainerFactory(new VectorContainerFactory());

        return dbi;
    }
}
