package streckenplan.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.SwingUtilities;

import types.Vector2d;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import streckenplan.implementations.Simulations;
import streckenplan.interfaces.Simulation;
import view.AnimatedView;
import view.Steppable;
import view.painter.Paintable;
import view.painter.Painter;

public class MainScript {
	private Simulation simulation = null;
	private SimulationProxy simulationProxy = null;
	private final AnimatedView view;

	private MainScript() {
		view = new AnimatedView(new Paintable() {
			@Override
			public void paint(Painter p) {
				Painter painter = p.translated(Vector2d.create(1. / 2, 1. / 2)).scaled(16 * 3).translated(Vector2d.create(1, 1));

				simulation.getPaintable().paint(painter);
			}
		}, new Steppable() {
			@Override
			public void step(double delta) {
				simulation.step(delta);
			}
		});

		view.getFrame().addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(@NotNull KeyEvent e) {
				if (e.getKeyChar() == 'r')
					loadScript();
			}
		});
	}

	private void start() {
		view.resize(540, 350);
		view.show();
		view.start(1. / 24);
		
		loadScript();
	}

	@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
	private void loadScript() {
		String scriptName = "main.lua";

		System.out.println(String.format("Loading script %s ...", scriptName));
		
		if (simulationProxy != null)
			simulationProxy.dispose();
		
		simulation = Simulations.createSimulation();

		LayoutProxy layoutProxy = new LayoutProxy(simulation.getLayout());
		simulationProxy = new SimulationProxy(view.getFrame());

		LuaValue _G = JsePlatform.standardGlobals();
		_G.set("layout", layoutProxy.layout());
		_G.set("simulation", simulationProxy.simulation());
		
		LuaValue script = null;
		InputStream input = MainScript.class.getResourceAsStream(scriptName);

		try {
			try {
				script = LoadState.load(input, scriptName, "t", _G);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		script.call();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainScript().start();
			}
		});
	}
}
