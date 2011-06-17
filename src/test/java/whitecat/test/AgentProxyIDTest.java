package whitecat.test;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import whitecat.core.agents.AgentProxyID;

/**
 * Tests the proxy ID generator.
 * 
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */
public class AgentProxyIDTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateByCopy() {
		// create two agent proxy id, check they are not equal and then copy one
		// on the other
		final AgentProxyID id1 = AgentProxyID.getNextAgentProxyID();
		AgentProxyID id2 = AgentProxyID.getNextAgentProxyID();

		if (id1.equals( id2 ))
			fail( "Generated IDs are equal!" );

		// now copy the id
		id2 = AgentProxyID.createByCopy( id1 );
		if (!id1.equals( id2 ))
			fail( "Error in copying the ids!" );

		if (id1.getSequenceID() != id2.getSequenceID())
			fail( "Error: the sequence numbers are not the same!" );
	}

	@Test
	public void testGetNextAgentProxyID() {
		// there must not be two agent proxy id generated equal

		// generate a random number of id to generate big enough...
		int numTest = new Random().nextInt();

		while (numTest < 0)
			numTest += 1000;
		while (numTest < 1)
			numTest *= 1000;
		while (numTest > 1000)
			numTest /= 1000;

		// create an array of generated ids
		final List<AgentProxyID> ids = new LinkedList<AgentProxyID>();

		// generate the ids
		for (int i = 0; i < numTest; i++){
			final AgentProxyID id = AgentProxyID.getNextAgentProxyID();
			if (ids.contains( id ))
				fail( "Found two identical IDs: " + id
						+ " generated at iteration " + i + " and "
						+ ids.indexOf( id ) );
			else ids.add( id );
		}

	}

}
