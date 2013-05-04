package streckenplan.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import static org.luaj.vm2.LuaValue.*;

final class SimulationProxy {
	private final JFrame frame;
	Map<String, LuaFunction> keyHandlers = new HashMap<String, LuaFunction>();
	public final KeyAdapter keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(@NotNull KeyEvent e) {
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
	};

	SimulationProxy(JFrame frame) {
		this.frame = frame;
		
		frame.addKeyListener(keyListener);
	}

	public LuaValue simulation() {
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
		
		//fieldsByProxy.put(res, field);

		return res;
	}
	
	private static Map<String, String> alternateKeyNames = new HashMap<String, String>();
	
	static {
		alternateKeyNames.put("⇧", "shift");
		alternateKeyNames.put("⌥", "alt");
		alternateKeyNames.put("⌘", "command");
		alternateKeyNames.put("⌃", "control");
		alternateKeyNames.put("⎋", "escape");
		alternateKeyNames.put("⏎", "return");
	}

	public void dispose() {
		frame.removeKeyListener(keyListener);
	}
}
