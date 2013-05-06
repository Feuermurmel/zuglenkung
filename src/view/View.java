package view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

import types.Vector2d;
import view.painter.*;

import static java.awt.RenderingHints.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public final class View {
	private final Paintable paintable;
	private final JFrame frame = new JFrame();

	private View(Paintable paintable) {
		this.paintable = paintable;
		
		frame.setSize(500, 320);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		frame.setContentPane(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				AffineTransform transform = new AffineTransform();
				Graphics2D g2 = (Graphics2D) g.create();
				Rectangle bounds = g.getClipBounds();

				transform.scale(1, -1);
				transform.translate(0, -frame.getContentPane().getHeight());
				g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_PURE); // this default to true when using -Dapple.awt.graphics.UseQuartz=true
				// TODO: test performance for the this setting
				//	g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
				g2.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);

				View.this.paintable.paint(DefaultGraphicsPainter.create(g2).transformed(transform));
			}
		});
	}

	public static View create(Paintable paintable) {
		return new View(paintable);
	}

	public void show() {
		frame.setVisible(true);
	}

	public void resize(int x, int y) {
		frame.setSize(x, y);
	}

	public Vector2d size() {
		return Vector2d.create(frame.getWidth(), frame.getHeight());
	}

	public void repaint() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.repaint();
			}
		});
	}
	
	public JFrame getFrame() {
		return frame;
	}
}
