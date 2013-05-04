package view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

import types.Vector2d;
import view.painter.*;

import static java.awt.RenderingHints.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class View {
	protected final Paintable paintable;
	protected final JFrame frame;
	protected final JPanel contentPane;

	public View(Paintable paintable) {
		this.paintable = paintable;

		contentPane = new JPanel() {
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

				View.this.paintable.paint(getPainter(g2).transformed(transform));
			}
		};

		frame = new JFrame();
		frame.setContentPane(contentPane);
		frame.setSize(500, 320);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	// FIXME: ugly
	protected Painter getPainter(Graphics2D g2) {
		return DefaultGraphicsPainter.create(g2);
	}

	public final void show() {
		frame.setVisible(true);
	}

	public final void resize(int x, int y) {
		frame.setSize(x, y);
	}

	public final Vector2d size() {
		return Vector2d.create(frame.getWidth(), frame.getHeight());
	}

	public final void repaint() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.repaint();
			}
		});
	}
	
	public final JFrame getFrame() {
		return frame;
	}
}
