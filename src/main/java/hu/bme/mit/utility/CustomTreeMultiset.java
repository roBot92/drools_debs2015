package hu.bme.mit.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.TreeMultiset;

public class CustomTreeMultiset {

	private static BigDecimal TWO = BigDecimal.valueOf(2);
	private TreeMultiset<BigDecimal> multiSet = TreeMultiset
			.create((n1, n2) ->  n1.compareTo(n2));

	public boolean add(BigDecimal element) {
		return multiSet.add(element);
	}

	public boolean addAll(Collection<? extends BigDecimal> elementsToAdd) {

		return multiSet.addAll(elementsToAdd);

	}

	public boolean remove(Object element) {
		return multiSet.remove(element);
	}

	public boolean removeAll(Collection<?> elementsToRemove) {
		return multiSet.removeAll(elementsToRemove);
	}

	public BigDecimal getMedian() {
		int listSize = multiSet.size();
		if (listSize == 0) {
			return BigDecimal.ZERO;
		}
		Iterator<BigDecimal> iterator = multiSet.iterator();
		if (listSize % 2 == 0) {
			for (int i = 0; i < listSize / 2 - 1; i++) {
				iterator.next();
			}
			BigDecimal number1 = iterator.next();
			BigDecimal number2 = iterator.next();
			return number1.add(number2).divide(TWO, 2, RoundingMode.HALF_UP);
		}
		for (int i = 0; i < listSize / 2; i++) {
			iterator.next();
		}
		return iterator.next();
	}

	public void clear() {
		multiSet.clear();
	}
}
