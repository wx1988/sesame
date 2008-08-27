/*
 * Copyright James Leigh (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.repository.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import info.aduna.iteration.Iteration;

import org.openrdf.StoreException;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.DelegatingRepositoryConnection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 * Delegates all calls to the delegate RepositoryConnection. Conditionally
 * processes add/remove/read to common base method to make them easier to
 * override.
 * 
 * @author James Leigh
 * @see #isDelegatingAdd()
 * @see #isDelegatingRemove()
 * @see #isDelegatingRead()
 */
public class RepositoryConnectionWrapper extends RepositoryConnectionBase implements
		DelegatingRepositoryConnection
{

	private RepositoryConnection delegate;

	public RepositoryConnectionWrapper(Repository repository) {
		super(repository);
	}

	public RepositoryConnectionWrapper(Repository repository, RepositoryConnection delegate) {
		this(repository);
		setDelegate(delegate);
	}

	public RepositoryConnection getDelegate()
		throws StoreException
	{
		return delegate;
	}

	public void setDelegate(RepositoryConnection delegate) {
		this.delegate = delegate;
	}

	/**
	 * If true then each add method will call
	 * {@link #addWithoutCommit(Resource, URI, Value, Resource[])}.
	 * 
	 * @return <code>false</code>
	 * @throws StoreException
	 */
	protected boolean isDelegatingAdd()
		throws StoreException
	{
		return true;
	}

	/**
	 * If true then the has/export/isEmpty methods will call
	 * {@link #getStatements(Resource, URI, Value, boolean, Resource[])}.
	 * 
	 * @return <code>false</code>
	 * @throws StoreException
	 */
	protected boolean isDelegatingRead()
		throws StoreException
	{
		return true;
	}

	/**
	 * If true then each remove method will call
	 * {@link #removeWithoutCommit(Resource, URI, Value, Resource[])}.
	 * 
	 * @return <code>false</code>
	 * @throws StoreException
	 */
	protected boolean isDelegatingRemove()
		throws StoreException
	{
		return true;
	}

	@Override
	public void add(File file, String baseURI, RDFFormat dataFormat, Resource... contexts)
		throws IOException, RDFParseException, StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(file, baseURI, dataFormat, contexts);
		}
		else {
			super.add(file, baseURI, dataFormat, contexts);
		}
	}

	@Override
	public void add(InputStream in, String baseURI, RDFFormat dataFormat, Resource... contexts)
		throws IOException, RDFParseException, StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(in, baseURI, dataFormat, contexts);
		}
		else {
			super.add(in, baseURI, dataFormat, contexts);
		}
	}

	@Override
	public void add(Iterable<? extends Statement> statements, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(statements, contexts);
		}
		else {
			super.add(statements, contexts);
		}
	}

	@Override
	public <E extends Exception> void add(Iteration<? extends Statement, E> statementIter,
			Resource... contexts)
		throws StoreException, E
	{
		if (isDelegatingAdd()) {
			getDelegate().add(statementIter, contexts);
		}
		else {
			super.add(statementIter, contexts);
		}
	}

	@Override
	public void add(Reader reader, String baseURI, RDFFormat dataFormat, Resource... contexts)
		throws IOException, RDFParseException, StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(reader, baseURI, dataFormat, contexts);
		}
		else {
			super.add(reader, baseURI, dataFormat, contexts);
		}
	}

	@Override
	public void add(Resource subject, URI predicate, Value object, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(subject, predicate, object, contexts);
		}
		else {
			super.add(subject, predicate, object, contexts);
		}
	}

	@Override
	public void add(Statement st, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(st, contexts);
		}
		else {
			super.add(st, contexts);
		}
	}

	@Override
	public void add(URL url, String baseURI, RDFFormat dataFormat, Resource... contexts)
		throws IOException, RDFParseException, StoreException
	{
		if (isDelegatingAdd()) {
			getDelegate().add(url, baseURI, dataFormat, contexts);
		}
		else {
			super.add(url, baseURI, dataFormat, contexts);
		}
	}

	@Override
	public void clear(Resource... contexts)
		throws StoreException
	{
		if (isDelegatingRemove()) {
			getDelegate().clear(contexts);
		}
		else {
			super.clear(contexts);
		}
	}

	@Override
	public void close()
		throws StoreException
	{
		getDelegate().close();
		super.close();
	}

	public void commit()
		throws StoreException
	{
		getDelegate().commit();
	}

	public void exportStatements(Resource subj, URI pred, Value obj, boolean includeInferred,
			RDFHandler handler, Resource... contexts)
		throws StoreException, RDFHandlerException
	{
		if (isDelegatingRead()) {
			getDelegate().exportStatements(subj, pred, obj, includeInferred, handler, contexts);
		}
		else {
			exportStatements(getStatements(subj, pred, obj, includeInferred, contexts), handler);
		}
	}

	public RepositoryResult<Resource> getContextIDs()
		throws StoreException
	{
		return getDelegate().getContextIDs();
	}

	public String getNamespace(String prefix)
		throws StoreException
	{
		return getDelegate().getNamespace(prefix);
	}

	public RepositoryResult<Namespace> getNamespaces()
		throws StoreException
	{
		return getDelegate().getNamespaces();
	}

	public RepositoryResult<Statement> getStatements(Resource subj, URI pred, Value obj,
			boolean includeInferred, Resource... contexts)
		throws StoreException
	{
		return getDelegate().getStatements(subj, pred, obj, includeInferred, contexts);
	}

	@Override
	public boolean hasStatement(Resource subj, URI pred, Value obj, boolean includeInferred,
			Resource... contexts)
		throws StoreException
	{
		if (isDelegatingRead()) {
			return getDelegate().hasStatement(subj, pred, obj, includeInferred, contexts);
		}
		return super.hasStatement(subj, pred, obj, includeInferred, contexts);
	}

	@Override
	public boolean hasStatement(Statement st, boolean includeInferred, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingRead()) {
			return getDelegate().hasStatement(st, includeInferred, contexts);
		}
		return super.hasStatement(st, includeInferred, contexts);
	}

	@Override
	public boolean isAutoCommit()
		throws StoreException
	{
		return getDelegate().isAutoCommit();
	}

	@Override
	public boolean isEmpty()
		throws StoreException
	{
		if (isDelegatingRead()) {
			return getDelegate().isEmpty();
		}
		return super.isEmpty();
	}

	@Override
	public boolean isOpen()
		throws StoreException
	{
		return getDelegate().isOpen();
	}

	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query, String baseURI)
		throws MalformedQueryException, StoreException
	{
		return getDelegate().prepareGraphQuery(ql, query, baseURI);
	}

	public Query prepareQuery(QueryLanguage ql, String query, String baseURI)
		throws MalformedQueryException, StoreException
	{
		return getDelegate().prepareQuery(ql, query, baseURI);
	}

	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query, String baseURI)
		throws MalformedQueryException, StoreException
	{
		return getDelegate().prepareTupleQuery(ql, query, baseURI);
	}

	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query, String baseURI)
		throws MalformedQueryException, StoreException
	{
		return getDelegate().prepareBooleanQuery(ql, query, baseURI);
	}

	@Override
	public void remove(Iterable<? extends Statement> statements, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingRemove()) {
			getDelegate().remove(statements, contexts);
		}
		else {
			super.remove(statements, contexts);
		}
	}

	@Override
	public <E extends Exception> void remove(Iteration<? extends Statement, E> statementIter,
			Resource... contexts)
		throws StoreException, E
	{
		if (isDelegatingRemove()) {
			getDelegate().remove(statementIter, contexts);
		}
		else {
			super.remove(statementIter, contexts);
		}
	}

	@Override
	public void remove(Resource subject, URI predicate, Value object, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingRemove()) {
			getDelegate().remove(subject, predicate, object, contexts);
		}
		else {
			super.remove(subject, predicate, object, contexts);
		}
	}

	@Override
	public void remove(Statement st, Resource... contexts)
		throws StoreException
	{
		if (isDelegatingRemove()) {
			getDelegate().remove(st, contexts);
		}
		else {
			super.remove(st, contexts);
		}
	}

	public void removeNamespace(String prefix)
		throws StoreException
	{
		getDelegate().removeNamespace(prefix);
	}

	public void clearNamespaces()
		throws StoreException
	{
		getDelegate().clearNamespaces();
	}

	public void rollback()
		throws StoreException
	{
		getDelegate().rollback();
	}

	@Override
	public void setAutoCommit(boolean autoCommit)
		throws StoreException
	{
		super.setAutoCommit(autoCommit);
		getDelegate().setAutoCommit(autoCommit);
	}

	public void setNamespace(String prefix, String name)
		throws StoreException
	{
		getDelegate().setNamespace(prefix, name);
	}

	public long size(Resource... contexts)
		throws StoreException
	{
		return getDelegate().size(contexts);
	}

	@Override
	protected void addWithoutCommit(Resource subject, URI predicate, Value object, Resource... contexts)
		throws StoreException
	{
		super.add(subject, predicate, object, contexts);
	}

	@Override
	protected void removeWithoutCommit(Resource subject, URI predicate, Value object, Resource... contexts)
		throws StoreException
	{
		super.remove(subject, predicate, object, contexts);
	}

	/**
	 * Exports all statements contained in the supplied statement iterator and
	 * all relevant namespace information to the supplied RDFHandler.
	 */
	protected void exportStatements(RepositoryResult<Statement> stIter, RDFHandler handler)
		throws StoreException, RDFHandlerException
	{
		try {
			handler.startRDF();
			// Export namespace information
			RepositoryResult<Namespace> nsIter = getNamespaces();
			try {
				while (nsIter.hasNext()) {
					Namespace ns = nsIter.next();
					handler.handleNamespace(ns.getPrefix(), ns.getName());
				}
			}
			finally {
				nsIter.close();
			}
			// Export statemnts
			while (stIter.hasNext()) {
				handler.handleStatement(stIter.next());
			}
			handler.endRDF();
		}
		finally {
			stIter.close();
		}
	}
}
