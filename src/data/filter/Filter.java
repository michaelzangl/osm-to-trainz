package data.filter;

/**
 * This is a filter that states if a object matches a given criterium.
 * 
 * @author michael
 * 
 * @param <T> The type of the object
 */
public interface Filter<T> {
	public boolean matches(T t);
}
