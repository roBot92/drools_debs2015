package hu.bme.mit.utility;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;

import org.kie.api.runtime.rule.AccumulateFunction;

public class MedianAccumulateFunction implements AccumulateFunction/*<MedianAccumulateFunction.MedianData>*/ {

	private static final long serialVersionUID = 1L;
	public CustomTreeMultiset multiSet = new CustomTreeMultiset();
	public static class MedianData implements Serializable {

		private static final long serialVersionUID = 1L;
		public CustomTreeMultiset multiSet = new CustomTreeMultiset();
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

	}

	@Override
	public void accumulate(Serializable context, Object value) {
		BigDecimal number = (BigDecimal) value;
		((MedianData)context).multiSet.add(number);

		
	}

	@Override
	public Serializable createContext() {
		return new MedianData();
	}

	@Override
	public Object getResult(Serializable context) throws Exception {
		return ((MedianData)context).multiSet.getMedian();
	}

	@Override
	public Class<?> getResultType() {
		return BigDecimal.class;
	}

	@Override
	public void init(Serializable context) throws Exception {
		((MedianData)context).multiSet.clear();
		
	}

	@Override
	public void reverse(Serializable context, Object value) throws Exception {
		((MedianData)context).multiSet.remove(value);
		
	}

	@Override
	public boolean supportsReverse() {
		return true;
	}
}
