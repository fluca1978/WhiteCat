package whitecat.test;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import whitecat.core.IProxyHandler;
import whitecat.core.WhiteCat;
import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.ProxyHandlerFactory;
import whitecat.core.agents.WCAgent;
import whitecat.core.exceptions.WCProxyException;
import whitecat.example.CloneableAgentProxy;
import whitecat.example.DBAgent;
import whitecat.example.DBProxy;

/**
 * Test the proxy handler.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class ProxyHandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCloneableProxy() throws WCProxyException {
		// create a new cloneable proxy to test
		final CloneableAgentProxy proxy1 = new CloneableAgentProxy(
				new DBAgent() );

		// create a new proxy
		final CloneableAgentProxy proxy2 = new CloneableAgentProxy(
				new DBAgent() );

		// check that the proxies are not the same!
		if ((proxy1.getIntegerVariable() == proxy2.getIntegerVariable())
				|| proxy1.getStringVariable().equals(
						proxy2.getStringVariable() )
				|| Arrays.equals(
						proxy1.getIntegerArray(),
						proxy2.getIntegerArray() ))
			fail( "Two agent proxies created equal but not yet cloned!" );

		// now clone one
		final IProxyHandler handler = WhiteCat.getProxyHandler();
		handler.setSourceProxy( proxy1 );
		handler.setDestinationProxy( proxy2 );
		handler.updateProxy();

		// check if all the extended state is the same
		if (!((proxy1.getIntegerVariable() == proxy2.getIntegerVariable())
				&& proxy1.getStringVariable().equals(
						proxy2.getStringVariable() ) && Arrays.equals(
				proxy1.getIntegerArray(),
				proxy2.getIntegerArray() )))
			fail( "Proxy extended state not cloned !!" );

	}

	@Test
	public void testProxyHandler() throws WCProxyException {
		// create a new proxy
		// and set some values of the proxy
		final WCAgent agent = new DBAgent();
		final AgentProxy proxy1 = new DBProxy( (DBAgent) agent );
		final DBProxy dbproxy1 = (DBProxy) proxy1;
		dbproxy1.setProperty1( new Random().nextInt() );
		dbproxy1.setProperty2( "A string test-" + dbproxy1.getProperty1() );

		// create a proxy handler
		final IProxyHandler<DBProxy> handler = ProxyHandlerFactory
				.getProxyHandler();

		// create a new proxy
		final DBProxy dbproxy2 = new DBProxy( new DBAgent() );
		// the two proxies should not be the same
		if (dbproxy2.equals( dbproxy1 )
				|| dbproxy2.getAgentProxyID().equals(
						dbproxy1.getAgentProxyID() )
				|| (dbproxy2.getProperty1() == dbproxy1.getProperty1())
				|| dbproxy2.getProperty2().equals( dbproxy1.getProperty2() ))
			fail( "Not handled proxies have same values!" );

		// now copy the proxies
		handler.setSourceProxy( dbproxy1 );
		handler.setDestinationProxy( dbproxy2 );
		handler.updateProxy();

		// not all values of the proxies must be the same, if the proxy status
		// must survive among role injections
		// there is nothing more to do, otherwise each proxy must provide an
		// initializeByCopy method
		if (!(dbproxy1.getAgentProxyID().equals( dbproxy2.getAgentProxyID() )))
			fail( "Handled proxies do not have same ID!" );

	}

}
