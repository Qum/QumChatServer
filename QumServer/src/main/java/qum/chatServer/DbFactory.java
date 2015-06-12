package qum.chatServer;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DbFactory {

    private ComboPooledDataSource ConnPool;

    private static DbFactory instance;
    static {
	try {
	    instance = new DbFactory();
	} catch (Exception e) {
	    throw new RuntimeException(
		    "��� �������� ������� �DbFactory� ��������� ������");
	}
    }

    private DbFactory() {

	ConnPool = new ComboPooledDataSource();
	ConnPool.setAutoCommitOnClose(true);

	ConnPool.setInitialPoolSize(10);
	ConnPool.setMinPoolSize(10);
	ConnPool.setMaxPoolSize(10);

	ConnPool.setAcquireRetryAttempts(0); // try to obtain connections
					     // indefinitely (0 = never quit)
	ConnPool.setAcquireRetryDelay(500); // 500 milliseconds wait before try
					    // to acquire connection again
	ConnPool.setCheckoutTimeout(0); // 0 = wait indefinitely for new
					// connection
	// if pool is exhausted
	ConnPool.setAcquireIncrement(5); // if pool is exhausted, get 5 more
					 // connections at a time
	// cause there is a "long" delay on acquire connection
	// so taking more than one connection at once will make connection
	// pooling
	// more effective.

	// this "connection_test_table" is automatically created if not already
	// there
	ConnPool.setAutomaticTestTable("connection_test_table");
	ConnPool.setTestConnectionOnCheckin(false);

	// testing OnCheckin used with IdleConnectionTestPeriod is faster than
	// testing on checkout

	ConnPool.setIdleConnectionTestPeriod(3600); // test idle connection
						    // every 60 sec
	ConnPool.setMaxIdleTime(60000); // 0 = idle connections never expire
	// *THANKS* to connection testing configured above
	// but I prefer to disconnect all connections not used
	// for more than 1 hour

	// enables statement caching, there is a "semi-bug" in c3p0 0.9.0 but in
	// 0.9.0.2 and later it's fixed
	ConnPool.setMaxStatementsPerConnection(100);

	ConnPool.setBreakAfterAcquireFailure(false); // never fail if any way
						     // possible
	// setting this to true will make
	// c3p0 "crash" and refuse to work
	// till restart thus making acquire
	// errors "FATAL" ... we don't want that
	// it should be possible to recover
	try {
	    ConnPool.setDriverClass("com.mysql.jdbc.Driver");
	} catch (PropertyVetoException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	ConnPool.setJdbcUrl("jdbc:mysql://localhost/qumserver");
	ConnPool.setUser("root");
	ConnPool.setPassword("toor");

	/* Test the connection */
	try {
	    ConnPool.getConnection().close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public static DbFactory getInstance() {
	return instance;
    }

    public Connection getCon() {
	Connection con = null;
	try {
	    con = ConnPool.getConnection();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return con;
    }

}
