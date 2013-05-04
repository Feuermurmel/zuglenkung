package types;

public final class Triple<T1, T2, T3> {
	public final T1 element1;
	public final T2 element2;
	public final T3 element3;
	
	Triple(T1 element1, T2 element2, T3 element3) {
		this.element1 = element1;
		this.element2 = element2;
		this.element3 = element3;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != Triple.class)
			return false;
		
		return element1.equals(((Triple<?, ?, ?>) obj).element1)
		   && element2.equals(((Triple<?, ?, ?>) obj).element2)
		   && element3.equals(((Triple<?, ?, ?>) obj).element3);
	}

	@Override
	public int hashCode() {
		return element1.hashCode()
		   + 31 * (element2.hashCode()
		      + 31 * element3.hashCode());
	}

	@Override
	public String toString() {
		return "(" + element1 + ", " + element2 + ", " + element3 + ")";
	}
}
