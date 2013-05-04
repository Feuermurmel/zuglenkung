package view.painter;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import types.Color;
import types.Vector2d;

public abstract class AbstractPainter implements Painter {
	@Override
	public final void draw(Color paint, Stroke stroke, Shape... shapes) {
		draw(paint, stroke, Arrays.asList(shapes));
	}

	@Override
	public final void fill(Color paint, Shape... shapes) {
		fill(paint, Arrays.asList(shapes));
	}

	@Override
	public final Painter translated(Vector2d vector) {
		return transformed(AffineTransform.getTranslateInstance(vector.x, vector.y));
	}

	@Override
	public final Painter rotated(double angle) {
		return transformed(AffineTransform.getRotateInstance(angle));
	}

	@Override
	public final Painter scaled(double factor) {
		return transformed(AffineTransform.getScaleInstance(factor, factor));
	}
}
