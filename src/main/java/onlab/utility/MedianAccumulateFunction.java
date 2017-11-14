package onlab.utility;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;

import org.kie.api.runtime.rule.AccumulateFunction;

public class MedianAccumulateFunction implements AccumulateFunction<MedianAccumulateFunction.MedianData> {

	public static class MedianData implements Serializable {

		private static final long serialVersionUID = 1L;
		public CustomTreeMultiset multiSet = new CustomTreeMultiset();
	}

	@Override
	public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {

	}

	@Override
	public void writeExternal(ObjectOutput arg0) throws IOException {

	}

	@Override
	public Class<?> getResultType() {
		return BigDecimal.class;
	}

	@Override
	public boolean supportsReverse() {
		return true;
	}

	@Override
	public MedianData createContext() {
		return new MedianData();
	}

	@Override
	public void init(MedianData context) throws Exception {
		context.multiSet.clear();

	}

	@Override
	public void accumulate(MedianData context, Object value) {
		BigDecimal number = (BigDecimal) value;
		context.multiSet.add(number);

	}

	@Override
	public void reverse(MedianData context, Object value) throws Exception {
		context.multiSet.remove(value);

	}

	@Override
	public Object getResult(MedianData context) throws Exception {
		return context.multiSet.getMedian();
	}

}
