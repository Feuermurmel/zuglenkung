package types;

public class Tuples {
	private Tuples() {
	}

	public static <T1, T2> Tuple<T1, T2> tuple(T1 element1, T2 element2) {
		return new Tuple<T1, T2>(element1, element2);
	}
	
	public static <T1, T2, T3> Triple<T1, T2, T3> triple(T1 element1, T2 element2, T3 element3) {
		return new Triple<T1, T2, T3>(element1, element2, element3);
	}
}
