package view.painter;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

import types.Color;
import types.Vector2d;

public final class DefaultGraphicsPainter implements Painter {
	private final Context context;
	private final AffineTransform transform;

	private DefaultGraphicsPainter(Context context, AffineTransform transform) {
		this.context = context;
		this.transform = transform;
	}

	@Override
	public Painter transformed(AffineTransform transform) {
		AffineTransform newTransform = new AffineTransform(this.transform);

		newTransform.concatenate(transform);

		return new DefaultGraphicsPainter(context, newTransform);
	}

	@Override
	public Rectangle2D getBounds() {
		Shape deviceBounds = context.graphics.getClip();
		
		try {
			return transform.createInverse().createTransformedShape(deviceBounds).getBounds2D();
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
	}

	private Graphics2D getGraphics(Color color) {
		checkContext();

		context.graphics.setPaint(color.asAWTColor());
		
		return context.graphics;
	}

	private Graphics2D getGraphics(Color color, Stroke stroke) {
		checkContext();

		context.graphics.setPaint(color.asAWTColor());
		context.graphics.setStroke(stroke);
		
		return context.graphics;
	}

	private void checkContext() {
		if (context.transform != transform) {
			context.graphics.setTransform(transform);
			context.transform = transform;
		}
	}

	public static Painter create(Graphics2D graphics) {
		return new DefaultGraphicsPainter(new Context(graphics), new AffineTransform());
	}

	@Override
	public void draw(Color paint, Stroke stroke, java.util.List<Shape> shapes) {
		Graphics2D g = getGraphics(paint, stroke);

		for (Shape i : shapes)
			g.draw(i);
	}

	@Override
	public void fill(Color paint, List<Shape> shapes) {
		Graphics2D g = getGraphics(paint);

		for (Shape i : shapes)
			g.fill(i);
	}

	// This would support sub-pixel anti-aliasing (only with -Dapple.awt.graphics.UseQuartz=true which may degrade performance substantially) but character placement is not as precise as with ShapeUtil.text()
	public void text(Vector2d position, Font font, Color color, String text) {
		Graphics g = getGraphics(color);
		
		g.setFont(font);
		
		g.drawString(text, (int) Math.round(position.x), (int) Math.round(position.y));
	}

	@Override
	public void draw(Color paint, Stroke stroke, Shape... shapes) {
		draw(paint, stroke, Arrays.asList(shapes));
	}

	@Override
	public void fill(Color paint, Shape... shapes) {
		fill(paint, Arrays.asList(shapes));
	}

	@Override
	public Painter translated(Vector2d vector) {
		return transformed(AffineTransform.getTranslateInstance(vector.x, vector.y));
	}

	@Override
	public Painter rotated(double angle) {
		return transformed(AffineTransform.getRotateInstance(angle));
	}

	@Override
	public Painter scaled(double factor) {
		return transformed(AffineTransform.getScaleInstance(factor, factor));
	}

	private static final class Context {
		public AffineTransform transform = null;
		public final Graphics2D graphics;

		private Context(Graphics2D graphics) {
			this.graphics = graphics;
		}
	}
}
