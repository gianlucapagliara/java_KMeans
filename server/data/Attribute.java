package data;

import java.io.Serializable;

/**
 * Questa classe implementa il concetto di attributo di una base di dati.
 * 
 * @author Gianluca Pagliara
 *
 */
public abstract class Attribute implements Serializable {
	protected String name;
	protected int id;

	/**
	 * Istanzia l'oggetto e inizializza il nome e l'id dell'attributo
	 * 
	 * @param name nome dell'attributo
	 * @param id id dell'attributo
	 */
	public Attribute(String name, int id) {
		this.name = name;
		this.id = id;
	}

	/**
	 * Restituisce il nome dell'attributo.
	 * 
	 * @return nome dell'attributo
	 */
	String getName() {
		return name;
	}

	/**
	 * Restituisce l'id dell'attributo.
	 * 
	 * @return id dell'attributo
	 */
	int getId() {
		return id;
	}

	/**
	 * Restituisce il nome dell'attributo.
	 */
	public String toString() {
		return name;
	}

}
