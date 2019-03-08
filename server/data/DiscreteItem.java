package data;

/**
 * Estende la classe {@link Item}, rappresentando una coppia
 * {@link DiscreteAttribute}-valore discreto.
 * 
 * @author Gianluca Pagliara
 *
 */
public class DiscreteItem extends Item {

	/**
	 * Istanzia l'oggetto invocando {@link Item#Item(Attribute, Object)}
	 * 
	 * @param attribute
	 *            attributo dell'item
	 * @param value
	 *            valore dell'item
	 */
	DiscreteItem(Attribute attribute, String value) {
		super(attribute, value);
	}

	/**
	 * Restituisce 0 se i due oggetti sono uguali, 1 altrimenti.
	 */
	@Override
	double distance(Object a) {
		return getValue().equals(a.toString()) ? 0 : 1;
	}

}
