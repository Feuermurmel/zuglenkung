package util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.List;

import types.Vector2d;

public class ShapeUtil {
	private ShapeUtil() {
	}
	
	private static final FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);
	
	public static Shape circle(Vector2d center, double radius) {
		return new Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2);
	}

	public static Shape line(List<Vector2d> points) {
		return createPolygonShape(points, false);
	}

	public static Shape line(Vector2d ... points) {
		return line(Arrays.asList(points));
	}

	public static Shape line(Vector2d p1, Vector2d p2) {
		// FIXME: Find a better way handle near-invisible shapes. This is not resolution independent
		//if (p2.sub(p1).length() < 1)
		//	return new Polygon();

		return new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
	}

	public static Shape arc(Vector2d center, double r, double angle1, double angle2) {
		return new Arc2D.Double(center.x - r, center.y - r, r * 2, r * 2, -angle1 * 360 / MathUtil.tau, (angle1 - angle2) * 360 / MathUtil.tau, Arc2D.OPEN);
	}
		
	public static Shape polygon(List<Vector2d> points) {
		return createPolygonShape(points, true);
	}
	
	public static Shape polygon(Vector2d ... points) {
		return polygon(Arrays.asList(points));
	}
	
	public static Shape text(Vector2d position, Font font, String string, double alignment) {
		GlyphVector glyphVector = font.createGlyphVector(fontRenderContext, string);

		AffineTransform transform = new AffineTransform(1, 0, 0, -1, position.x - glyphVector.getLogicalBounds().getWidth() * alignment, position.y);
		
		return transform.createTransformedShape(glyphVector.getOutline());
	}

	public static Shape text(Vector2d position, Font font, String string) {
		return text(position, font, string, 0f);
	}

	private static Shape createPolygonShape(List<Vector2d> points, boolean closed) {
		Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD, points.size());

		path.moveTo(points.get(0).x, points.get(0).y);

		for (Vector2d i : points.subList(1, points.size()))
			path.lineTo(i.x, i.y);

		if (closed)
			path.closePath();

		return path;
	}
}
