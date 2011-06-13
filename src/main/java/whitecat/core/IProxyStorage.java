package whitecat.core;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.AgentProxyID;

public interface IProxyStorage {

    /**
     * A method to lock the specified proxy. The method increases the locking counter
     * of the specified proxy.
     * Please take care of the synchronization method: all the operations that act on the storage itself,
     * such as placing, searching, removing a proxy are synchronized on 'this' object, while all other
     * operations are not synchronized. This method, as well as it counterpart, cannot be declared synchronized
     * because in such situation a wait call on the agent proxy's status lock will cause the thread to sleep
     * in a synchronized method (the current one) and therefore no one thread can resume the locked proxy.
     * @param proxyToLock the proxy to lock
     * @param lockCurrentThread true if the current thread must be locked now, false if only the status
     * of the agent proxy must be set to locked.
     * @param timeToLock the max amount of time to lock the thread if the lockCurrentThread flag is true. If a zero value
     * is passed the thread waits undefinitely.
     */
    @SuppressWarnings("null")
    public void lockAgentProxy(AgentProxy proxyToLock,
	    boolean lockCurrentThread, long timeToLock);

    /**
     * A mehtod to unlock the specified proxy (i.e., to decrease the locking counter).
     * Please take care of the synchronization method: all the operations that act on the storage itself,
     * such as placing, searching, removing a proxy are synchronized on 'this' object, while all other
     * operations are not synchronized. This method, as well as it counterpart, cannot be declared synchronized
     * because in such situation a wait call on the agent proxy's status lock will cause the thread to sleep
     * in a synchronized method (the current one) and therefore no one thread can resume the locked proxy.
     * @param proxyToUnlock the proxy to lock
     * @param unlockThread true if the current thread must be unlocked
     */
    @SuppressWarnings("null")
    public void unlockAgentProxy(AgentProxy proxyToUnlock, boolean unlockThread);

    /**
     * A proxy is locked if the locking counter is greater than zero.
     * @param proxyToCheck
     * @return
     */
    public boolean isAgentProxyLocked(AgentProxy proxyToCheck);

    /**
     * Adds a new agent proxy into the map creating a new status if needed.
     * @param proxy the agent proxy to store
     */
    public void storeAgentProxy(AgentProxy proxy);

    /**
     * Removes a proxy from the storage map. This is useful if the proxy has been destroyed.
     * Before removing the proxy, the method unlocks the proxy so that waiters can be notified.
     * @param proxy the proxy to remove
     */
    public void deleteAgentProxy(AgentProxy proxy);

    /**
     * Provides the last updated proxy in the storage for the specified id.
     * @param id
     * @return
     */
    public AgentProxy getLastUpdatedAgentProxy(AgentProxyID id);

}