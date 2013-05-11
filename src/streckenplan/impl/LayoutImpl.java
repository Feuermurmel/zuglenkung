package streckenplan.impl;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.*;

import streckenplan.api.*;
import streckenplan.sched.Scheduler;
import streckenplan.sched.Steppable;
import types.*;
import util.MathUtil;
import view.painter.Paintable;
import view.painter.Painter;

import static util.ShapeUtil.*;
import static util.StrokeUtil.*;

final class LayoutImpl implements Paintable, Layout, Steppable {
	private final Scheduler scheduler;
	private final Map<Tuple<Integer, Integer>, FieldImpl> fieldsByPosition = new HashMap<Tuple<Integer, Integer>, FieldImpl>();
	private final Map<Field, TrainImpl> trainsOccupyingField = new HashMap<Field, TrainImpl>();
	private final List<TrainImpl> trains = new ArrayList<TrainImpl>();
	private final Map<Field, List<Runnable>> waitUntilFieldFreeRunnables = new HashMap<Field, List<Runnable>>();
	private final Map<Field, List<Runnable>> waitUntilFieldOccupiedRunnables = new HashMap<Field, List<Runnable>>();

	LayoutImpl(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void paint(Painter p) {
		Rectangle2D bounds = p.getBounds();

		int minRow = (int) Math.floor(bounds.getMinY() / fieldHeightRatio);
		int maxRow = (int) Math.floor(bounds.getMaxY() / fieldHeightRatio + 2);

		for (int row = minRow; row < maxRow; row += 1) {
			int minColumn = (int) Math.floor(bounds.getMinX() - row / 2.);
			int maxColumn = (int) Math.floor(bounds.getMaxX() - row / 2.) + 2;

			for (int column = minColumn; column < maxColumn; column += 1) {
				FieldImpl field = fieldsByPosition.get(Tuples.tuple(column, row));
				Painter fieldPainter = p.translated(positionOfField(column, row));

				if (field != null && trainsOccupyingField.containsKey(field))
					fieldPainter.fill(occupiedColor, fieldShape);

				fieldPainter.draw(Color.black, basic(1f / 64), fieldBorderLines);

				if (field != null)
					field.paint(fieldPainter);
			}
		}

		for (TrainImpl i : trains) {
			i.paint(p);
		}

		//Rectangle2D bounds = p2.getBounds();
		//BufferedImage image = ((DefaultGraphicsPainter) p2).getGraphics().getDeviceConfiguration().createCompatibleImage((int) bounds.getWidth() * 20, (int) bounds.getHeight() * 20, Transparency.TRANSLUCENT);
		//
		//Painter p = DefaultGraphicsPainter.create((Graphics2D) image.getGraphics()).scaled(20);
		//
		//int minRow = (int) Math.floor(bounds.getMinY() / fieldHeightRatio);
		//int maxRow = (int) Math.floor(bounds.getMaxY() / fieldHeightRatio + 2);
		//
		//for (int row = minRow; row < maxRow; row += 1) {
		//	int minColumn = (int) Math.floor(bounds.getMinX() - row / 2.);
		//	int maxColumn = (int) Math.floor(bounds.getMaxX() - row / 2.) + 2;
		//
		//	for (int column = minColumn; column < maxColumn; column += 1) {
		//		FieldImpl field = fieldsByPosition.get(Tuples.tuple(column, row));
		//		Painter fieldPainter = p.translated(positionOfField(column, row));
		//
		//		if (field != null && trainsOccupyingField.containsKey(field))
		//			fieldPainter.fill(occupiedColor, fieldShape);
		//
		//		fieldPainter.draw(Color.black, basic(1f / 64), fieldBorderShape);
		//
		//		if (field != null)
		//			field.paint(fieldPainter );
		//	}
		//}
		//
		//for (TrainImpl i : trains)
		//	i.paint(p);
		//
		//((DefaultGraphicsPainter) p2).getGraphics().drawImage(image, 0, 0, null);
	}

	@Override
	public void step(double delta) {
		trainsOccupyingField.clear();

		for (TrainImpl i : trains) {
			i.step(delta);
		}

		checkWaitUntilState();
	}

	@Override
	public FieldImpl getField(int column, int row) {
		Tuple<Integer, Integer> position = Tuples.tuple(column, row);
		FieldImpl field = fieldsByPosition.get(position);

		if (field == null) {
			field = new FieldImpl(scheduler, this, column, row);

			fieldsByPosition.put(position, field);
		}

		return field;
	}

	public Train addTrain(TrackPosition position, double engineCarLength) {
		TrainImpl train = new TrainImpl(this, position);

		train.addCar(engineCarLength);
		trains.add(train);

		return train;
	}

	public void occupyField(Field field, TrainImpl train) {
		TrainImpl currentOccupyingTrain = trainsOccupyingField.get(field);

		if (currentOccupyingTrain != null && currentOccupyingTrain != train) {
			train.setCrashedWithOtherTrain();
			currentOccupyingTrain.setCrashedWithOtherTrain();
		}

		trainsOccupyingField.put(field, train);
	}

	public boolean isFieldOccupied(FieldImpl field) {
		return trainsOccupyingField.get(field) != null;
	}

	private void checkWaitUntilState() {
		checkWaitUntilState(waitUntilFieldFreeRunnables, false);
		checkWaitUntilState(waitUntilFieldOccupiedRunnables, true);
	}

	public void waitUntilFieldState(FieldImpl field, boolean occupied, Runnable runnable) {
		Map<Field, List<Runnable>> map;

		if (occupied)
			map = waitUntilFieldOccupiedRunnables;
		else
			map = waitUntilFieldFreeRunnables;

		List<Runnable> runnables = map.get(field);

		if (runnables == null) {
			runnables = new ArrayList<Runnable>();

			map.put(field, runnables);
		}

		runnables.add(runnable);
	}

	private void checkWaitUntilState(Map<Field, List<Runnable>> map, boolean occupied) {
		List<List<Runnable>> runnables = new ArrayList<List<Runnable>>();
		List<Field> processedFields = new ArrayList<Field>();

		for (Map.Entry<Field, List<Runnable>> i : map.entrySet()) {
			if (trainsOccupyingField.containsKey(i.getKey()) == occupied) {
				processedFields.add(i.getKey());
				runnables.add(i.getValue());
			}
		}

		map.keySet().removeAll(processedFields);

		for (List<Runnable> i : runnables)
			for (Runnable j : i)
				j.run();
	}

	private static final double fieldHeightRatio = Math.sqrt(3) / 2;
	private static final Color occupiedColor = Color.red.brighten(.7f);
	private static final Shape fieldShape;
	private static final Shape[] fieldBorderLines;

	static {
		List<Vector2d> vertices = new ArrayList<Vector2d>();

		for (int i = 0; i < 6; i += 1) {
			vertices.add(Vector2d.fromAngle((i + .5) * MathUtil.tau / 6, 1 / Math.sqrt(3)));
		}

		fieldShape = polygon(vertices);
		
		List<Shape> fieldBorderLines2 = new ArrayList<Shape>();
		
		// These vertices are "before" the field, in order they are painted. Thus painting these after the field background color paint them above the background of adjacent fields.
		for (int i = 2; i < 5; i += 1)
			fieldBorderLines2.add(line(vertices.get(i), vertices.get(i + 1)));
		
		fieldBorderLines = fieldBorderLines2.toArray(new Shape[0]);
	}

	public static Vector2d positionOfField(int column, int row) {
		return Vector2d.create(column + row / 2., row * fieldHeightRatio);
	}
}
