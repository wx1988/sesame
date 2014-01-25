/* 
 * Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. 
 *
 * Aduna licenses this file to you under the terms of the Aduna BSD 
 * License (the "License"); you may not use this file except in compliance 
 * with the License. See the LICENSE.txt file distributed with this work 
 * for the full License.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.openrdf.http.server.repository.transaction;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openrdf.repository.RepositoryConnection;

/**
 * @author jeen
 */
public class ActiveTransactionRegistry {

	private static final ActiveTransactionRegistry singleton = new ActiveTransactionRegistry();

	public static ActiveTransactionRegistry getInstance() {
		return singleton;
	}

	private ActiveTransactionRegistry() {
		// private constructor, implementing singleton pattern
	}

	private final ConcurrentMap<UUID, RepositoryConnection> activeConnections = new ConcurrentHashMap<UUID, RepositoryConnection>();

	private final ConcurrentMap<UUID, Lock> transactionLocks = new ConcurrentHashMap<UUID, Lock>();

	public void register(UUID transactionId, RepositoryConnection conn)
		throws IllegalArgumentException
	{
		if (activeConnections.putIfAbsent(transactionId, conn) != null) {
			throw new IllegalArgumentException("transaction with id " + transactionId.toString()
					+ " already registered.");
		}
	}

	public void deregister(UUID transactionId, RepositoryConnection conn)
		throws IllegalArgumentException
	{
		synchronized (activeConnections) {
			if (!activeConnections.remove(transactionId, conn)) {
				throw new IllegalArgumentException("transaction with id " + transactionId.toString()
						+ " not registered.");
			}
			transactionLocks.remove(transactionId);
		}
	}

	public RepositoryConnection getTransactionConnection(UUID transactionId)
		throws InterruptedException
	{

		RepositoryConnection conn = null;

		Lock txnLock = null;
		synchronized (transactionLocks) {
			txnLock = transactionLocks.get(transactionId);
			if (txnLock == null) {
				txnLock = new ReentrantLock();
				transactionLocks.put(transactionId, txnLock);
			}
		}

		txnLock.lockInterruptibly();
		conn = activeConnections.get(transactionId);

		return conn;
	}

	public void returnTransactionConnection(UUID transactionId) {
		Lock txnLock = transactionLocks.get(transactionId);
		if (txnLock != null) {
			txnLock.unlock();
		}
		else {
			throw new IllegalArgumentException("no lock available for " + transactionId);
		}
	}
}
