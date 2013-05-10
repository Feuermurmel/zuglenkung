import java.awt.*;
import java.awt.geom.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import util.MathUtil;

import static java.awt.RenderingHints.*;

public class Test {
	private Test() {
	}

	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		final JFrame frame = new JFrame();

		frame.setSize(300, 300);
		frame.setContentPane(new Container() {
			@Override
			public void paint(Graphics graphics) {
				Graphics2D g2 = (Graphics2D) graphics;

				g2.setTransform(AffineTransform.getRotateInstance(MathUtil.tau / 100));
				g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_PURE);

				long endTime = System.nanoTime() + 1000 * 1000 * 1000;
				int count = 0;

				Shape line;

				if (true) {
					Path2D.Double path = new Path2D.Double();

					path.moveTo(10, 10);
					path.lineTo(50, 50);

					line = path;

					//Polygon polygon =   
				} else {
					line = new Line2D.Double(10, 10, 50, 50 / .9);
				}

				g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

				while (System.nanoTime() < endTime) {
					for (int i = 0; i < 1000; i += 1) {
						g2.draw(line);
						count += 1;
					}
				}

				System.out.println(count);
			}
		});

		frame.setVisible(true);

		for (int i = 0; i < 10; i += 1) {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					frame.repaint();
				}
			});
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				frame.dispose();
			}
		});
	}
}
