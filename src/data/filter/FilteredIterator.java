package data.filter;

import java.util.Iterator;

/**
 * This iterator filters an other iterator.
 * @author michael
 *
 * @param <E>
 */
public class FilteredIterator<E> implements Iterator<E> {
	private E current = null;
	private final Iterator<E> toFilter;
	private final Filter<E> filter;

	public FilteredIterator(Iterator<E> toFilter, Filter<E> filter) {
		this.toFilter = toFilter;
		this.filter = filter;
		current = findNextFiltered();
	}

	private E findNextFiltered() {
		while (toFilter.hasNext()) {
			E next = toFilter.next();
			if (filter.matches(next)) {
				return next;
			}
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		return current != null;
	}

	@Override
	public E next() {
		E ret = current;
		current = findNextFiltered();
		return ret;
	}

	@Override
	public void remove() {
		toFilter.remove();
	}

}
