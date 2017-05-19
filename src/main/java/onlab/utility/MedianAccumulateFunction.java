package onlab.utility;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.rule.AccumulateFunction;

//TODO lista helyett sorted multiset
public class MedianAccumulateFunction implements AccumulateFunction {

	private static class MedianData implements Serializable{
		
		private static final long serialVersionUID = 1L;
		public List<Number> list = new ArrayList<Number>(); 
	}
	
	
	
	@Override
	public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
		
		
	}

	@Override
	public void writeExternal(ObjectOutput arg0) throws IOException {
		
	}

	@Override
	public void accumulate(Serializable context, Object object) {
		MedianData md = (MedianData) context;
		Number number = (Number) object;
		md.list.add(number);
	}

	@Override
	public Serializable createContext() {
		return new MedianData();
	}

	@Override
	public Object getResult(Serializable context) throws Exception {
		MedianData md = (MedianData) context; 
		md.list.sort((n1, n2) -> Double.compare(n1.doubleValue(), n2.doubleValue()));
		
		int listSize = md.list.size();
		if(listSize % 2 == 0){
			Number number1 = md.list.get(listSize/2-1);
			Number number2 = md.list.get(listSize/2);
			return BigDecimal.valueOf((number1.doubleValue()+number2.doubleValue())/2);
		}
		
		return md.list.get(listSize/2);
		
	}

	@Override
	public Class<?> getResultType() {
		return Number.class;
	}

	@Override
	public void init(Serializable context) throws Exception {
		MedianData md = (MedianData) context;
		md.list.clear();
		
	}

	@Override
	public void reverse(Serializable context, Object object) throws Exception {
		MedianData md = (MedianData) context;
		md.list.remove(object);		
	}

	@Override
	public boolean supportsReverse() {
		return true;
	}

}
