package types;

public final class Tuple<T1, T2> {
	public final T1 element1;
	public final T2 element2;

	Tuple(T1 element1, T2 element2) {
		this.element1 = element1;
		this.element2 = element2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != Tuple.class)
			return false;

		return element1.equals(((Tuple<?, ?>) obj).element1)
				&& element2.equals(((Tuple<?, ?>) obj).element2);
	}

	@Override
	public int hashCode() {
		return element1.hashCode()
				+ 31 * element2.hashCode();
	}

	@Override
	public String toString() {
		return "(" + element1 + ", " + element2 + ")";
	}
}
