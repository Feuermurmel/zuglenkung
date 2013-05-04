package streckenplan.implementations;

import java.awt.Shape;
import java.awt.Stroke;

import types.Orientation2d;
import types.Vector2d;
import streckenplan.interfaces.Car;
import streckenplan.interfaces.Train;
import types.Color;
import util.ShapeUtil;
import util.StrokeUtil;
import view.painter.Paintable;
import view.painter.Painter;

final class CarImpl implements Car, Paintable {
	private final TrainImpl train;
	private final double length;
	private final Shape carShape;
	private Orientation2d currentOrientation = null;
	
	CarImpl(TrainImpl train, double length) {
		this.train = train;
		this.length = length;
		carShape = createCarShape(length);
	}

	@Override
	public Train getTrain() {
		return train;
	}

	@Override
	public double getLength() {
		return length;
	}
	
	@Override
	public void paint(Painter p) {
		Painter painter = p.translated(currentOrientation.position()).rotated(currentOrientation.angle);
		Color fillColor;
		
		if (train.hasCrashed())
			fillColor = crashedFillColor;
		else
			fillColor = CarImpl.fillColor;
		
		painter.fill(fillColor, carShape);
		painter.draw(borderColor, borderStroke, carShape);
	}
	
	public void updateOrientation(TrackPosition pos1, TrackPosition centerPos, TrackPosition pos2) {
		Vector2d point1 = pos1.positionOnPath();
		Vector2d point2 = pos2.positionOnPath();
		Vector2d center = centerPos.positionOnPath().scale(1f / 2).add(point1.scale(1f / 4)).add(point2.scale(1f / 4));
		
		double angle = point2.sub(point1).angle();
		
		currentOrientation = Orientation2d.create(center, angle);
	}

	private static final Stroke borderStroke = StrokeUtil.basic(1f / 32);
	private static final Color borderColor = Color.black;
	private static final Color fillColor = Color.blue.brighten(.7f);
	private static final Color crashedFillColor = Color.red.darken(0.2f);

	private static Shape createCarShape(double length) {
		double l = length / 2 - 1. / 16;
		
		return StrokeUtil.basic(1f / 5).createStrokedShape(ShapeUtil.line(Vector2d.fromAngle(0, -l), Vector2d.fromAngle(0, l)));
	}
}
