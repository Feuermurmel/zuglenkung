package streckenplan.proxy;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import streckenplan.api.Simulation;
import streckenplan.api.Track;
import types.Color;

import static org.luaj.vm2.LuaValue.*;

final class SimulationProxyImpl implements SimulationProxy {
	private final Simulation simulation;
	Map<String, LuaFunction> keyHandlers = new HashMap<String, LuaFunction>();

	SimulationProxyImpl(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public LuaValue getProxyTable() {
		LuaValue res = tableOf();

		res.set("addKeyHandler", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				String key = arg1.checkjstring();
				LuaFunction fn = arg2.checkclosure(); // Does this also allow native function that can be call()ed?

				keyHandlers.put(key, fn);

				return NIL;
			}
		});

		res.set("setColor", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				Track track = Proxies.getAssociatedValue(arg1, Track.class);
				Color color = colorFromTable(arg2);
				
				track.setColor(color);
				
				return NIL;
			}
		});

		res.set("schedule", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				double delay = arg1.checkdouble();
				Runnable runnable = Proxies.runnableFromLuaFunction(arg2);

				simulation.getScheduler().post(delay, runnable);

				return NIL;
			}
		});

		//fieldsByProxy.put(res, field);

		return res;
	}

	private static Color colorFromTable(LuaValue table) {
		return Color.create((float) table.get(1).checkdouble(), (float) table.get(2).checkdouble(), (float) table.get(3).checkdouble());
	}

	private static final Map<String, String> alternateKeyNames = new HashMap<String, String>();

	static {
		alternateKeyNames.put("⇧", "shift");
		alternateKeyNames.put("⌥", "alt");
		alternateKeyNames.put("⌘", "command");
		alternateKeyNames.put("⌃", "control");
		alternateKeyNames.put("⎋", "escape");
		alternateKeyNames.put("⏎", "return");
	}

	@Override
	public void handleKeyPressed(KeyEvent e) {
		String name;
		char character = e.getKeyChar();

		if (character == KeyEvent.CHAR_UNDEFINED) {
			String keyText = KeyEvent.getKeyText(e.getKeyCode());

			if (alternateKeyNames.containsKey(keyText))
				name = alternateKeyNames.get(keyText);
			else
				name = keyText;
		} else {
			name = String.format("%s", character);
		}

		//System.out.println(String.format("%s, %s, %s", e.getKeyChar(), KeyEvent.getKeyText(e.getKeyCode()), name));

		LuaFunction handler = keyHandlers.get(name);

		if (handler != null)
			handler.call();
	}
}
