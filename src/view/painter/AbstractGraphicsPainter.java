package view.painter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

import types.Color;
import types.Vector2d;
import view.site.ZoneListener;

public abstract class AbstractGraphicsPainter extends AbstractPainter {
	@Override
	public final void draw(Color paint, Stroke stroke, List<Shape> shapes) {
		Graphics2D g = getGraphics(paint, stroke);

		for (Shape i : shapes)
			g.draw(i);
	}

	@Override
	public final void fill(Color paint, List<Shape> shapes) {
		Graphics2D g = getGraphics(paint);

		for (Shape i : shapes)
			g.fill(i);
	}

	@Override
	public final void zone(Shape shape, ZoneListener listener) {
		getZoneContext().add(getTransform().createTransformedShape(shape), listener);
	}
	
	// This would support sub-pixel anti-aliasing (only with -Dapple.awt.graphics.UseQuartz=true which may degrade performance substantially) but character placement is not as precise as with ShapeUtil.text()
	public final void text(Vector2d position, Font font, Color color, String text) {
		Graphics g = getGraphics(color);
		
		g.setFont(font);
		
		g.drawString(text, (int) Math.round(position.x), (int) Math.round(position.y));
	}

	protected abstract Graphics2D getGraphics(Color color);
	protected abstract Graphics2D getGraphics(Color color, Stroke stroke);
	protected abstract ZoneContext getZoneContext();
	protected abstract AffineTransform getTransform();

	public interface ZoneContext {
		void add(Shape shape, ZoneListener listener);
	}
}
