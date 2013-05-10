package streckenplan.proxy;

import java.awt.event.KeyEvent;

public interface SimulationProxy extends LuaProxy {
	void handleKeyPressed(KeyEvent e);
}
