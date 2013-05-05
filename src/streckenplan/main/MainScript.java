package streckenplan.main;

import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import streckenplan.implementations.Simulations;
import streckenplan.interfaces.Simulation;
import types.Vector2d;
import util.FileUtil;
import view.AnimatedView;
import view.Steppable;
import view.painter.Paintable;
import view.painter.Painter;

public class MainScript {
	private final File scriptFile;
	private Simulation simulation;
	private SimulationProxy simulationProxy;
	private long lastScriptFileLastModifiedTime = 0;
	private final AnimatedView view;

	private MainScript(File scriptFile) {
		this.scriptFile = scriptFile;
		
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

		// So that there's always a simulation, will be replaced when a new script version has been loaded successfully.
		simulation = Simulations.createSimulation();
		simulationProxy = new SimulationProxy(view.getFrame());
	}

	private void start() {
		view.resize(540, 350);
		view.show();
		view.start(1. / 24);

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
		LayoutProxy layoutProxy = new LayoutProxy(simulation.getLayout());
		SimulationProxy simulationProxy = new SimulationProxy(view.getFrame());
		
		LuaValue _G = JsePlatform.standardGlobals();
		_G.set("layout", layoutProxy.layout());
		_G.set("simulation", simulationProxy.simulation());

		try {
			LoadState.load(new ByteArrayInputStream(script), name, "t", _G).call();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		simulationProxy.dispose();
		
		this.simulation = simulation;
		this.simulationProxy = simulationProxy;
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
