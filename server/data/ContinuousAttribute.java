package data;

/**
 * Questa classe estende {@linkplain Attribute} e rappresenta un attributo
 * continuo.
 * 
 * @author Gianluca Pagliara
 *
 */
public class ContinuousAttribute extends Attribute {

	// Estremi dell'intervallo di valori (dominio) che l'attributo puo' realmente
	// assumere
	private double max;
	private double min;

	/**
	 * Istanzia l'oggetto e inzializza il nome, l'id e i valori minimi e massimi che
	 * l'attributo può assumere.
	 * 
	 * @param name
	 *            nome dell'attributo
	 * @param id
	 *            id dell'attributo
	 * @param min
	 *            valore minimo che può assumere l'attributo
	 * @param max
	 *            valore massimo che può assumere l'attributo
	 */
	public ContinuousAttribute(String name, int id, double min, double max) {
		super(name, id);
		this.min = min;
		this.max = max;
	}

	/**
	 * Normalizza il valore v.
	 * 
	 * @param v
	 *            valore da normalizzare
	 * @return valore normalizzato
	 */
	double getScaledValue(double v) {
		return (v - min) / (max - min);
	}

}
