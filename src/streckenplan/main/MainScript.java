package streckenplan.main;

import java.awt.event.*;
import java.io.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import streckenplan.api.Simulation;
import streckenplan.impl.Simulations;
import streckenplan.proxy.*;
import types.Vector2d;
import util.FileUtil;
import view.View;
import view.painter.Paintable;
import view.painter.Painter;

public class MainScript {
	private final File scriptFile;
	private Simulation simulation = null;
	private SimulationProxy simulationProxy = null;
	private Timer simulationTimer = null;
	private long lastScriptFileLastModifiedTime = 0;
	private final View view;

	private MainScript(File scriptFile) {
		this.scriptFile = scriptFile;

		// So that there's always a simulation, will be replaced when a new script version has been loaded successfully.
		simulation = Simulations.createSimulation();
		simulationProxy = Proxies.createSimulationProxy(simulation);

		view = View.create(new Paintable() {
			@Override
			public void paint(Painter p) {
				if (simulation != null) {
					Painter painter = p.translated(Vector2d.create(1. / 2, 1. / 2)).scaled(16 * 3).translated(Vector2d.create(1, 1));

					simulation.getPaintable().paint(painter);	
				}
			}
		});

		view.getFrame().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(@NotNull KeyEvent e) {
				if (simulationProxy != null)
					simulationProxy.handleKeyPressed(e);
			}
		});
	}

	private void start() {
		view.resize(540, 350);
		view.show();

		new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(@NotNull ActionEvent e) {
				long lastModified = scriptFile.lastModified();

				if (lastScriptFileLastModifiedTime != lastModified) {
					lastScriptFileLastModifiedTime = lastModified;

					try {
						loadScript(FileUtil.readFileBytes(scriptFile), scriptFile.getName());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void loadScript(byte[] script, String name) {
		System.out.println(String.format("Reloading script %s ...", name));

		Simulation simulation = Simulations.createSimulation();
		SimulationProxy simulationProxy = Proxies.createSimulationProxy(simulation);
		LuaProxy layoutProxy = Proxies.createLayoutProxy(simulation.getLayout());

		LuaValue _G = JsePlatform.standardGlobals();
		_G.set("layout", layoutProxy.getProxyTable());
		_G.set("simulation", simulationProxy.getProxyTable());

		if (script != null) {
			try {
				LoadState.load(new ByteArrayInputStream(script), name, "t", _G).call();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		this.simulation = simulation;
		this.simulationProxy = simulationProxy;
		
		if (simulationTimer != null)
			simulationTimer.stop();

		final float delta = 1f / 24;

		simulationTimer = new Timer((int) (delta * 1000), new ActionListener() {
			@Override
			public void actionPerformed(@NotNull ActionEvent e) {
				MainScript.this.simulation.step(delta);
				view.repaint();
			}
		});
		
		simulationTimer.start();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				File scriptFile = new File("res/main.lua");

				new MainScript(scriptFile).start();
			}
		});
	}
}
