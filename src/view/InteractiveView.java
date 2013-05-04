package view;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

import types.Vector2d;
import view.painter.*;
import view.site.ZoneListener;

public class InteractiveView extends View {
	private ZoneListener hoveredListener = null;
	private ZoneContext lastZoneContex = null;
	
	public InteractiveView(Paintable paintable) {
		super(paintable);
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				processNewPosition(Vector2d.create(e.getX(), e.getY()));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				processNewPosition(null);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				processNewPosition(Vector2d.create(e.getX(), e.getY()));
			}
		};

		contentPane.addMouseListener(mouseAdapter);
		contentPane.addMouseMotionListener(mouseAdapter);
	}

	@Override
	protected Painter getPainter(Graphics2D g2) {
		lastZoneContex = new ZoneContext();
		return DefaultGraphicsPainter.create(g2, lastZoneContex);
	}

	private void processNewPosition(Vector2d position) {
		ZoneListener newHoveredListener = lastZoneContex.getMouseListenerAt(position);

		if (newHoveredListener != hoveredListener) {
			if (hoveredListener != null)
				hoveredListener.mouseExited();
			
			if (newHoveredListener != null)
				newHoveredListener.mouseEntered();

			hoveredListener = newHoveredListener;
			
			contentPane.repaint();
		}
	}
	
	public static final class ZoneContext implements DefaultGraphicsPainter.ZoneContext {
		public final List<Entry> listeners = new ArrayList<Entry>();

		@Override
		public void add(Shape shape, ZoneListener listener) {
			listeners.add(new Entry(shape, listener));
		}

		private static final class Entry {
			public final Shape shape;
			public final ZoneListener listener;

			private Entry(Shape shape, ZoneListener listener) {
				this.shape = shape;
				this.listener = listener;
			}
		}

		// FIXME: ugly
		public ZoneListener getMouseListenerAt(Vector2d position) {
			if (position == null)
				return null;

			for (int i = listeners.size() - 1; i >= 0; i -= 1) {
				Entry listener = listeners.get(i);

				if (listener.shape.contains(new Point2D.Double(position.x, position.y)))
					return listener.listener;
			}

			return null;
		}
	}
}
