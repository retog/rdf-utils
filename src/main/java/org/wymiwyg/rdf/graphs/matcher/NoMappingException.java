package org.wymiwyg.rdf.graphs.matcher;

/**
 * @author reto
 * 
 */
public class NoMappingException extends Exception {
	private static final long serialVersionUID = -8045859220979897268L;

	public NoMappingException() {
		super();
	}
	
	//increases performance
	public Throwable fillInStackTrace() {
		return this;
	}


}