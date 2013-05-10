import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.JFrame;

import static java.awt.RenderingHints.*;

public class Test2 {
	public static void main(String[] args) {
		JFrame frame = new JFrame();

		frame.setSize(300, 300);
		frame.setContentPane(new Container() {
			@Override
			public void paint(Graphics graphics) {
				Graphics2D g2 = (Graphics2D) graphics;
				g2.setStroke(new BasicStroke(5));
				g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_PURE);

				Path2D.Double path = new Path2D.Double();
				path.moveTo(200, 100);
				path.lineTo(100, 100);
				path.lineTo(101, 100.3);

				g2.draw(path);
			}
		});

		frame.setVisible(true);
	}
}
