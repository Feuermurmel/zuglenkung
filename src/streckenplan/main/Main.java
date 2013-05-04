//package streckenplan_hexa.main;
//
//import geometry.Vector2d;
//import streckenplan_hexa.implementations.*;
//import streckenplan_hexa.interfaces.Aspect;
//import streckenplan_hexa.interfaces.Direction;
//import view.AnimatedView;
//import view.View;
//import view.painter.Paintable;
//import view.painter.Painter;
//
//public class Main {
//	private Main() {
//	}
//
//	public static void main(String[] args) {
//		final Layout layout = Layout.createRectangular(12, 8);
//
//		//for (Field.Element i : field)
//		//	i.tile.addTrack(Track.leftCurve(RandomUtil.randomElement(Direction.values())));
//		//
//		//layout.blockAt(-1, 2).add
//		//
//		//layout.get
//		
//		for (int i = 0; i < 12; i += 1) {
//			layout.fieldAtPosition(i - 1, 2).addTrack(TrackArrangements.straight(Direction.three));
//			layout.fieldAtPosition(i, 1).addTrack(TrackArrangements.straight(Direction.three));
//		}
//		
//		layout.fieldAtPosition(5, 2).addTrack(TrackArrangements.leftCurve(Direction.three));
//		
//		for (int i = 3; i < 6; i += 1) {
//			layout.fieldAtPosition(5, i).addTrack(TrackArrangements.rightCurve(Direction.four));
//			
//			if (i < 5)
//				layout.fieldAtPosition(5, i).addTrack(TrackArrangements.straight(Direction.four));
//			
//			int end = 10 - i / 2;
//			
//			for (int j = 6; j < end; j += 1)
//				layout.fieldAtPosition(j, i).addTrack(TrackArrangements.straight(Direction.three));
//			
//			layout.fieldAtPosition(end, i).addTrack(TrackArrangements.terminus(Direction.three, i % 2 == 0));
//		}
//
//		//layout.fieldAtPosition(3, 2).addSignal(SignalArrangement.dwarfSignal(Direction.zero, Direction.three), Fahrbegriff.fahrt);
//		//layout.fieldAtPosition(5, 2).addSignal(SignalArrangement.dwarfSignal(Direction.one, Direction.three), Fahrbegriff.haltErwarten);
//		//layout.fieldAtPosition(5, 5).addSignal(SignalArrangement.dwarfSignal(Direction.zero, Direction.four), Fahrbegriff.halt);
//
//		layout.fieldAtPosition(6, 1).addSignal(SignalArrangement.dwarfSignal(Direction.zero, Direction.three), Aspect.fahrt);
//		layout.fieldAtPosition(8, 1).addSignal(SignalArrangement.dwarfSignal(Direction.zero, Direction.three), Aspect.haltErwarten);
//		layout.fieldAtPosition(10, 1).addSignal(SignalArrangement.dwarfSignal(Direction.zero, Direction.three), Aspect.halt);
//
//		final Track track = layout.fieldAtPosition(4, 2).interfaces().get(Direction.three).nextActiveTrack();
//		
//		new Runnable() {
//			private final Train train = new Train(new TrackPosition(track, 0), 4);
//			
//			private final View view = new AnimatedView(new Paintable() {
//				@Override
//				public void paint(Painter p) {
//					Painter painter = p.translated(Vector2d.create(.5, .5)).scaled(40 * view.size().x / 540).translated(Vector2d.create(1, 1));
//					
//					layout.paint(painter);
//
//					train.paint(painter);
//				}
//			}, new Runnable() {
//				@Override
//				public void run() {
//					float delta = 1f / 24;
//					
//					layout.step(delta);
//					
//					try {
//						train.step(delta);
//					} catch (TrackPosition.NoMoreTrackException ignored) {
//					}
//				}
//			}, 1. / 24);
//			
//			@Override
//			public void run() {
//				view.resize(540, 350);
//				view.show();
//			}
//		}.run();
//	}
//}
