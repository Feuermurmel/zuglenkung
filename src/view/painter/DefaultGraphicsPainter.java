package view.painter;

import java.awt.*;
import java.awt.geom.*;

import types.Color;
import view.InteractiveView;

public final class DefaultGraphicsPainter extends AbstractGraphicsPainter {
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

	@Override
	protected Graphics2D getGraphics(Color color) {
		checkContext();

		context.graphics.setPaint(color.asAWTColor());
		
		return context.graphics;
	}

	@Override
	protected Graphics2D getGraphics(Color color, Stroke stroke) {
		checkContext();

		context.graphics.setPaint(color.asAWTColor());
		context.graphics.setStroke(stroke);
		
		return context.graphics;
	}

	@Override
	protected ZoneContext getZoneContext() {
		return context.zones;
	}

	@Override
	protected AffineTransform getTransform() {
		return context.transform;
	}

	private void checkContext() {
		if (context.transform != transform) {
			context.graphics.setTransform(transform);
			context.transform = transform;
		}
	}

	public static Painter create(Graphics2D graphics, InteractiveView.ZoneContext zoneContext) {
		return new DefaultGraphicsPainter(new Context(graphics, zoneContext), new AffineTransform());
	}

	public static Painter create(Graphics2D graphics) {
		return create(graphics, null);
	}

	private static final class Context {
		public AffineTransform transform = null;
		public final Graphics2D graphics;
		public final InteractiveView.ZoneContext zones;

		public Context(Graphics2D graphics, InteractiveView.ZoneContext zones) {
			this.graphics = graphics;
			this.zones = zones;
		}
	}
}
