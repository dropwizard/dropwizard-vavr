package io.dropwizard.vavr.jdbi;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import io.vavr.collection.Array;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.List;
import io.vavr.collection.Queue;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import io.vavr.collection.Tree;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class VavrDBIFactoryTest {
    private final Environment env = new Environment("test", Jackson.newObjectMapper(),
            Validators.newValidator(), new MetricRegistry(), null);

    private TaskDao dao;

    @Before
    public void setupTests() throws IOException {
        final DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setDriverClass("org.h2.Driver");
        dataSourceFactory.setUrl("jdbc:h2:mem:test-" + System.currentTimeMillis() + "?user=sa");
        dataSourceFactory.setInitialSize(1);
        final DBI dbi = new VavrDBIFactory().build(env, dataSourceFactory, "test");
        try (Handle h = dbi.open()) {
            h.execute("CREATE TABLE tasks (" +
                    "id INT PRIMARY KEY, " +
                    "assignee VARCHAR(255) NOT NULL, " +
                    "start_date TIMESTAMP, " +
                    "end_date TIMESTAMP, " +
                    "comments VARCHAR(1024) " +
                    ")");
        }
        dao = dbi.onDemand(TaskDao.class);

        dao.insert(100, Option.some("Name 1"), LocalDate.parse("2017-08-24"), Option.none(), Option.none());
        dao.insert(200, Option.some("Name 2"), LocalDate.parse("2017-08-25"), Option.none(), Option.some("To be done"));
    }

    @Test
    public void testPresent() {
        final Option<LocalDate> endDate = Option.some(LocalDate.parse("2015-11-03"));
        dao.insert(1, Option.some("John Hughes"), LocalDate.now(), endDate, Option.none());

        assertThat(dao.findEndDateById(1)).isEqualTo(endDate);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, Option.some("Kate Johansen"), LocalDate.now(), Option.none(), Option.some("To be done"));

        assertThat(dao.findEndDateById(2).isEmpty()).isTrue();
    }

    @Test
    public void testArray() {
        assertThat(dao.findStartDatesAsArray()).hasSize(2);
    }

    @Test
    public void testQueue() {
        assertThat(dao.findStartDatesAsQueue()).hasSize(2);
    }

    @Test
    public void testStream() {
        assertThat(dao.findStartDatesAsStream()).hasSize(2);
    }

    @Test
    public void testVector() {
        assertThat(dao.findStartDatesAsVector()).hasSize(2);
    }

    @Test
    public void testSeq() {
        assertThat(dao.findStartDatesAsSeq()).hasSize(2);
    }

    @Test
    public void testIndexedSeq() {
        assertThat(dao.findStartDatesAsIndexedSeq()).hasSize(2);
    }

    @Test
    public void testList() {
        assertThat(dao.findStartDatesAsList()).hasSize(2);
    }

    @Test
    public void testSet() {
        assertThat(dao.findStartDatesAsSet()).hasSize(2);
    }

    @Test
    public void testTree() {
        assertThat(dao.findStartDatesAsTree()).hasSize(2);
    }

    interface TaskDao {
        @SqlUpdate("INSERT INTO tasks(id, assignee, start_date, end_date, comments) VALUES (:id, :assignee, :start_date, :end_date, :comments)")
        void insert(@Bind("id") int id,
                    @Bind("assignee") Option<String> assignee,
                    @Bind("start_date") LocalDate startDate,
                    @Bind("end_date") Option<LocalDate> endDate,
                    @Bind("comments") Option<String> comments);

        @SqlQuery("SELECT end_date FROM tasks WHERE id = :id")
        @SingleValueResult
        Option<LocalDate> findEndDateById(@Bind("id") int id);

        @SqlQuery("SELECT start_date FROM tasks")
        Array<LocalDate> findStartDatesAsArray();

        @SqlQuery("SELECT start_date FROM tasks")
        Queue<LocalDate> findStartDatesAsQueue();

        @SqlQuery("SELECT start_date FROM tasks")
        Stream<LocalDate> findStartDatesAsStream();

        @SqlQuery("SELECT start_date FROM tasks")
        Vector<LocalDate> findStartDatesAsVector();

        @SqlQuery("SELECT start_date FROM tasks")
        Seq<LocalDate> findStartDatesAsSeq();

        @SqlQuery("SELECT start_date FROM tasks")
        IndexedSeq<LocalDate> findStartDatesAsIndexedSeq();

        @SqlQuery("SELECT start_date FROM tasks")
        List<LocalDate> findStartDatesAsList();

        @SqlQuery("SELECT start_date FROM tasks")
        Set<LocalDate> findStartDatesAsSet();

        @SqlQuery("SELECT start_date FROM tasks")
        Tree<LocalDate> findStartDatesAsTree();
    }
}