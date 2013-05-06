package view.painter;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import types.Color;
import types.Vector2d;

public interface Painter {
	void draw(Color paint, Stroke stroke, Shape... shapes);

	void draw(Color paint, Stroke stroke, List<Shape> shapes);

	void fill(Color paint, Shape... shapes);

	void fill(Color paint, List<Shape> shapes);

	Painter transformed(AffineTransform transform);

	Painter translated(Vector2d vector);

	Painter rotated(double angle);

	Painter scaled(double factor);

	Rectangle2D getBounds();
}
