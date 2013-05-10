package streckenplan.proxy;

import java.util.*;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.*;
import streckenplan.api.*;

import static org.luaj.vm2.LuaValue.*;

final class LayoutProxyImpl implements LuaProxy {
	private final Layout layout;

	LayoutProxyImpl(Layout layout) {
		this.layout = layout;
	}

	@Override
	public LuaValue getProxyTable() {
		LuaValue res = tableOf();

		res.set("getCenterField", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return field(layout.getField(0, 0));
			}
		});

		return res;
	}

	private LuaValue field(final Field field) {
		LuaValue res = tableOf();

		res.set("getNeighbour", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				Direction direction = directionsByName.get(arg1.checkjstring());
				int distance = arg2.optint(1);

				if (direction == null)
					throw new IllegalArgumentException();

				return field(field.getNeighbour(direction, distance));
			}
		});

		res.set("getEdge", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				Direction direction = directionsByName.get(arg.checkjstring());

				return edge(field.getEdge(direction));
			}
		});

		res.set("isOccupied", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return valueOf(field.isOccupied());
			}
		});

		res.set("awaitState", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				String state = arg1.checkjstring();
				Runnable runnable = Proxies.runnableFromOptionalLuaFunction(arg2);

				boolean occupied;

				if (state.equals("free"))
					occupied = false;
				else if (state.equals("occupied"))
					occupied = true;
				else
					throw new IllegalArgumentException();

				field.waitUntilState(occupied, runnable);

				return NIL;
			}
		});

		//fieldsByProxy.put(res, field);

		return res;
	}

	private LuaValue edge(final Edge edge) {
		LuaValue res = tableOf();

		res.set("reverse", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return edge(edge.reverse());
			}
		});

		res.set("getField", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return field(edge.getField());
			}
		});

		res.set("getDirection", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(directionNames.get(edge.getDirection()));
			}
		});

		res.set("getTracks", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				List<LuaValue> tracks = new ArrayList<LuaValue>();

				for (Track i : edge.getTracks()) {
					tracks.add(track(i));
				}

				return LuaValue.listOf(tracks.toArray(new LuaValue[tracks.size()]));
			}
		});

		res.set("getActiveTrack", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				Track track = edge.getActiveTrack();

				if (track == null)
					return NIL;
				else
					return track(track);
			}
		});

		res.set("addTrack", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				String trackType = arg.checkjstring();
				Track track;

				if (trackType.equals("curve-left")) {
					track = edge.getField().addTrack(edge.getDirection(), edge.getDirection().turn(1));
				} else if (trackType.equals("straight")) {
					track = edge.getField().addTrack(edge.getDirection(), edge.getDirection());
				} else if (trackType.equals("curve-right")) {
					track = edge.getField().addTrack(edge.getDirection(), edge.getDirection().turn(-1));
				} else if (trackType.equals("end-short")) {
					track = edge.getField().addEndTrack(edge.getDirection(), EndTrackLength.short_);
				} else if (trackType.equals("end-long")) {
					track = edge.getField().addEndTrack(edge.getDirection(), EndTrackLength.long_);
				} else {
					throw new IllegalArgumentException(String.format("Unknown track type: %s", trackType));
				}

				return track(track);
			}
		});

		return res;
	}

	private LuaValue track(final Track track) {
		LuaValue res = Proxies.createAssociatedTable(track);

		res.set("reverse", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return track(track.reverse());
			}
		});

		res.set("getField", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return field(track.getField());
			}
		});

		res.set("getStartEdge", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				Edge edge = track.getStartEdge();

				if (edge == null)
					return NIL;

				return edge(edge);
			}
		});

		res.set("getEndEdge", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				Edge edge = track.getEndEdge();

				if (edge == null)
					return NIL;

				return edge(edge);
			}
		});

		res.set("addTrain", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				double engineCarLength = arg.checkdouble();

				Train train = track.addTrain(engineCarLength);

				return train(train);
			}
		});

		res.set("addSignal", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				String signalType = arg.checkjstring();
				SignalType type;

				if (signalType.equals("dwarf"))
					type = SignalType.dwarf;
				else if (signalType.equals("system-l"))
					type = SignalType.systemLHauptsignal;
				else
					throw new IllegalArgumentException();

				return signal(track.addSignal(type));
			}
		});

		//res.set("getType", new ZeroArgFunction() {
		//	@Override
		//	public LuaValue call() {
		//		TrackType type = track.reverse().getType();
		//
		//		if (type == TrackType.leftCurve)
		//			return valueOf("curve-left");
		//		else if (type == TrackType.straight)
		//			return valueOf("straight");
		//		else if (type == TrackType.rightCurve)
		//			return valueOf("curve-right");
		//		else if (type == TrackType.facingEndpoint)
		//			return valueOf("end-short"); // TODO: Does not distinguish endpoint length
		//		else if (type == TrackType.reverseEndpoint)
		//			return valueOf("end-short"); // TODO: Does not distinguish endpoint length
		//		else
		//			throw new RuntimeException();
		//	}
		//});

		res.set("getAffectingSignals", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				List<LuaValue> signals = new ArrayList<LuaValue>();

				for (Signal i : track.getAffectingSignals()) {
					signals.add(signal(i));
				}

				return listOf(signals.toArray(new LuaValue[signals.size()]));
			}
		});

		res.set("isActive", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return valueOf(track.isActive());
			}
		});

		res.set("setActive", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				track.activate(Proxies.runnableFromOptionalLuaFunction(arg));

				return NIL;
			}
		});

		return res;
	}

	private LuaValue train(final Train train) {
		LuaValue res = tableOf();

		res.set("addCar", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				double length = arg.checkdouble();

				Car car = train.addCar(length);

				return car(car);
			}
		});

		res.set("setTargetSpeed", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				double speed = arg.checkdouble();

				train.setTargetSpeed(speed);

				return NIL;
			}
		});

		//res.set("getPosition", new ZeroArgFunction() {
		//	@Override
		//	public LuaValue call() {
		//		LuaValue res = tableOf();
		//		positi aspect = signal.getCurrentAspect();
		//
		//		res.set("distance", aspect.distance);
		//		res.set("speed", aspect.speed);
		//
		//		return res;
		//	}
		//});

		return res;
	}

	private LuaValue car(Car car) {
		LuaValue res = tableOf();

		// TODO: Add stuff ...

		return res;
	}

	private LuaValue signal(final Signal signal) {
		LuaValue res = tableOf();

		res.set("getField", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return field(signal.getField());
			}
		});

		res.set("getAffectedTracks", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				List<LuaValue> tracks = new ArrayList<LuaValue>();

				for (Track i : signal.getAffectedTracks()) {
					tracks.add(track(i));
				}

				return listOf(tracks.toArray(new LuaValue[tracks.size()]));
			}
		});

		res.set("getAspect", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				LuaValue res = tableOf();
				Aspect aspect = signal.getCurrentAspect();

				res.set("distance", aspect.distance);
				res.set("speed", aspect.speed);

				return res;
			}
		});

		res.set("setAspect", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				signal.setCurrentAspect(Aspect.create(arg.get("distance").checkdouble(), arg.get("speed").checkdouble()));

				return NIL;
			}
		});

		return res;
	}

	private static final Map<Direction, String> directionNames = new EnumMap<Direction, String>(Direction.class);
	private static final Map<String, Direction> directionsByName = new HashMap<String, Direction>();

	static {
		directionNames.put(Direction.zero, "r");
		directionNames.put(Direction.one, "ru");
		directionNames.put(Direction.two, "lu");
		directionNames.put(Direction.three, "l");
		directionNames.put(Direction.four, "ld");
		directionNames.put(Direction.fife, "rd");

		for (Map.Entry<Direction, String> i : directionNames.entrySet()) {
			directionsByName.put(i.getValue(), i.getKey());
		}
	}
}
