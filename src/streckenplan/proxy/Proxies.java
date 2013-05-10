package streckenplan.proxy;

import java.util.Map;
import java.util.WeakHashMap;

import org.luaj.vm2.LuaValue;
import streckenplan.api.Layout;
import streckenplan.api.Simulation;
import util.RunnableUtil;

public class Proxies {
	private Proxies() {
	}

	private static final Map<LuaValue, Object> associatedValues = new WeakHashMap<LuaValue, Object>();

	static Runnable runnableFromOptionalLuaFunction(LuaValue arg) {
		if (arg.isnil()) {
			return RunnableUtil.emptyRunnable;
		} else {
			return runnableFromLuaFunction(arg);
		}
	}

	static Runnable runnableFromLuaFunction(final LuaValue arg) {
		if (arg.isfunction()) {
			return new Runnable() {
				@Override
				public void run() {
					arg.call();
				}
			};
		} else {
			throw new IllegalArgumentException(String.format("Argument is not a function: %s", arg));
		}
	}

	public static LayoutProxyImpl createLayoutProxy(Layout layout) {
		return new LayoutProxyImpl(layout);
	}

	public static SimulationProxy createSimulationProxy(Simulation simulation) {
		return new SimulationProxyImpl(simulation);
	}

	static <T> LuaValue createAssociatedTable(T associatedValue) {
		LuaValue res = LuaValue.tableOf();

		associatedValues.put(res, associatedValue);

		return res;
	}

	static <T> T getAssociatedValue(LuaValue table, Class<T> type) {
		Object value = associatedValues.get(table);

		if (value == null)
			throw new IllegalStateException("This table has no associated value.");

		try {
			return type.cast(value);
		} catch (Exception ignored) {
			throw new IllegalArgumentException(String.format("This table's assicated value is not of type %s", type.getName()));
		}
	}
}
