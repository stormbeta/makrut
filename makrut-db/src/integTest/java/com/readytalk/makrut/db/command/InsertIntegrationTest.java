package com.readytalk.makrut.db.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import com.readytalk.makrut.db.BasicTestDataFramework;
import com.readytalk.makrut.db.MakrutDBFactory;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class InsertIntegrationTest extends BasicTestDataFramework {

	private final ScalarHandler<Integer> handler = new ScalarHandler<Integer>();

	private MakrutDBFactory makrutFactory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setUp();

		makrutFactory = injector.getInstance(MakrutDBFactory.class);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void insert_WithGivenKey_ReturnsKey() throws Exception {
		Insert<Integer> command = makrutFactory.insert(handler, "insert into test (id, val) VALUES (4, 'test4')");

		assertEquals(4, command.submitAndGet().intValue());
	}

	@Test
	public void insert_WithNoKey_ReturnsNextKey() throws Exception {
		Insert<Integer> command = makrutFactory.insert(handler, "insert into test (val) VALUES ('test4')");

		assertEquals(4, command.submitAndGet().intValue());
	}

	@Test
	public void insert_WithDuplicateKey_Throws23SQLState() throws Exception {
		Insert<Integer> command = makrutFactory.insert(handler, "insert into test (id, val) VALUES (1, 'test4')");

		try {
			command.submitAndGet();
			fail("Did not throw exception.");
		} catch (ExecutionException ex) {
			if (!(ex.getCause() instanceof SQLException)) {
				fail("Not a SQLException.");
			}

			SQLException sqle = (SQLException) ex.getCause();

			assertEquals("23", sqle.getSQLState().substring(0, 2));
		}
	}
}
