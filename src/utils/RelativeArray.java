package utils;

public class RelativeArray<T> {

	private int begin, end;
	private Object [] array;
	
	public RelativeArray(int begin, int end) {
		this.begin = begin;
		this.end = end;
		array = new Object[Math.abs(end - begin + 1)];
	}
	
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) (array[begin + index]);
	}
	
	public void set(T value, int index) {
		array[index] = value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("[");
		for (Object o : array) {
			builder.append((T)o.toString() + ", ");
		}
		builder.append("]");
		
		return builder.toString();
	}
}
