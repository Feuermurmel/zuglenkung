package view;

import java.awt.event.*;
import javax.swing.*;

import view.painter.Paintable;

public final class AnimatedView extends View {
	private final Steppable steppable;

	public AnimatedView(Paintable paintable, Steppable steppable) {
		super(paintable);
		
		this.steppable = steppable;
	}

	public void start(final double updateInterval) {
		final Timer timer = new Timer((int) Math.ceil(updateInterval * 1000), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				steppable.step(updateInterval);
				frame.repaint();
			}
		});

		timer.setCoalesce(false);
		timer.start();

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				timer.stop();
			}
		});
	}
}
