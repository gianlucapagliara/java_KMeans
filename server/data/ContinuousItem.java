package data;

/**
 * Questa classe estente {@link Item} e rappresenta un generico item continuo,
 * ossia una coppia attributo-valore continuo.
 * 
 * @author Gianluca Pagliara
 *
 */
public class ContinuousItem extends Item {

	/**
	 * Istanzia l'oggetto e inizializza l'attributo e il valore.
	 * 
	 * @param attribute
	 *            attributo dell'item
	 * @param value
	 *            valore dell'item
	 */
	public ContinuousItem(Attribute attribute, Double value) {
		super(attribute, value);
	}

	/**
	 * Restituisce la distanza dall'oggetto obj, ossia la differenza in valore
	 * assoluto dei valori normalizzati dei due oggetti.
	 */
	@Override
	double distance(Object obj) {
		double thisScaledValue = ((ContinuousAttribute) this.getAttribute()).getScaledValue((double) this.getValue());
		double objScaledValue = ((ContinuousAttribute) ((Item) obj).getAttribute())
				.getScaledValue(((double) ((Item) obj).getValue()));
		return Math.abs(thisScaledValue - objScaledValue);
	}

}
