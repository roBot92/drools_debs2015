package onlab.utility;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.TreeMultiset;

public class CustomTreeMultiset {

	private TreeMultiset<BigDecimal> multiSet = TreeMultiset
			.create((n1, n2) -> Double.compare(n1.doubleValue(), n2.doubleValue()));

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
			Number number1 = iterator.next();
			Number number2 = iterator.next();
			return BigDecimal.valueOf((number1.doubleValue() + number2.doubleValue()) / 2);
		}
		for (int i = 0; i < listSize / 2; i++) {
			iterator.next();
		}
		return iterator.next();
	}

}
