package streckenplan.impl;

import java.util.*;

import org.jetbrains.annotations.Nullable;
import streckenplan.api.*;
import streckenplan.sched.Scheduler;
import types.*;
import util.ComparatorUtil;
import util.MathUtil;
import view.painter.Painter;

final class FieldImpl implements Field {
	private final Scheduler scheduler;
	private final LayoutImpl layout;
	private final int column;
	private final int row;
	private final Map<SignalArrangement, SignalImpl> signalsByArrangement = new HashMap<SignalArrangement, SignalImpl>();
	private final HashMap<TrackArrangement, TrackArrangementInstance> trackArrangementInstances = new HashMap<TrackArrangement, TrackArrangementInstance>();

	private SwitchState switchState = new PassiveSwitchState(null);
	private Map<TrackImpl, List<SignalImpl>> affectingSignalsByTrack = Collections.emptyMap();
	private Map<SignalImpl, List<TrackImpl>> affectedTracksBySignal = Collections.emptyMap();
	private Map<Direction, List<Track>> tracksWithStartDirection = Collections.emptyMap(); // direction is looking into the field

	FieldImpl(Scheduler scheduler, LayoutImpl layout, int column, int row) {
		this.scheduler = scheduler;
		this.layout = layout;
		this.column = column;
		this.row = row;
	}

	public void paint(Painter p) {
		for (TrackArrangement i : trackArrangementInstances.keySet())
			i.paintBorder(p);
		
		List<Map.Entry<TrackArrangement, TrackArrangementInstance>> trackArrangements = new ArrayList<Map.Entry<TrackArrangement, TrackArrangementInstance>>(trackArrangementInstances.entrySet());
		
		Collections.sort(trackArrangements, new ComparatorUtil.KeyComparator<Map.Entry<TrackArrangement, TrackArrangementInstance>, Float>() {
			@Override
			public Float getKey(Map.Entry<TrackArrangement, TrackArrangementInstance> value) {
				return -value.getValue().color.getBrightness();
			}
		});
		
		for (Map.Entry<TrackArrangement, TrackArrangementInstance> i : trackArrangements)
			i.getKey().paintFill(p, i.getValue().color);

		switchState.paint(p);

		for (SignalImpl i : signalsByArrangement.values())
			i.paint(p);
	}

	public Vector2d getPosition() {
		return LayoutImpl.positionOfField(column, row);
	}

	@Override
	public FieldImpl getNeighbour(Direction direction, int distance) {
		return layout.getField(column + direction.offsetx * distance, row + direction.offsety * distance);
	}

	@Override
	public Edge getEdge(Direction direction) {
		return new EdgeImpl(this, direction.reverse());
	}

	@Override
	public boolean isOccupied() {
		return layout.isFieldOccupied(this);
	}

	// TODO: Check for overlap
	public Signal addSignal(Direction limitEntryAngle, Direction limitExitAngle, SignalType type) {
		SignalArrangement arrangement = SignalArrangements.getArrangement(limitEntryAngle, limitExitAngle, type);
		SignalImpl signal = new SignalImpl(this, arrangement);

		signalsByArrangement.put(arrangement, signal);
		updateCachedTrackData();

		return signal;
	}

	@Override
	public Track addTrack(Direction startDirection, Direction endDirection) {
		// TODO: Check for overlap
		int diff = endDirection.minus(startDirection);
		TrackArrangement arrangement;

		if (diff == 5)
			arrangement = TrackArrangements.rightCurve(startDirection);
		else if (diff == 0)
			arrangement = TrackArrangements.straight(startDirection);
		else if (diff == 1)
			arrangement = TrackArrangements.leftCurve(startDirection);
		else
			throw new IllegalArgumentException();

		switchState = new PassiveSwitchState(arrangement);

		return addTrack(arrangement, arrangement.getStartDirection() != startDirection);
	}

	@Override
	public Track addEndTrack(Direction startDirection, EndTrackLength length) {
		TrackArrangement arrangement = TrackArrangements.terminus(startDirection, length == EndTrackLength.long_);

		return addTrack(arrangement, false);
	}

	@Override
	public void waitUntilState(boolean occupied, Runnable runnable) {
		layout.waitUntilFieldState(this, occupied, runnable);
	}

	private TrackImpl addTrack(TrackArrangement arrangement, boolean reversed) {
		TrackImpl forward = new TrackImpl(this, arrangement, false);
		TrackImpl reverse = new TrackImpl(this, arrangement, true);

		trackArrangementInstances.put(arrangement, new TrackArrangementInstance(forward, reverse));
		
		updateCachedTrackData();

		if (reversed)
			return reverse;
		else
			return forward;
	}

	// TODO: Maybe pre-calculate these?
	public List<Track> getTracksFromEdge(Direction startDirection) {
		return Collections.unmodifiableList(tracksWithStartDirection.get(startDirection));
	}

	@Nullable
	public TrackImpl getActiveTrackFromEdge(Direction startDirection) {
		TrackArrangement activeArrangement = switchState.getActiveArrangement();

		if (activeArrangement == null) {
			return null;
		} else {
			TrackArrangementInstance arrangementInstance = trackArrangementInstances.get(activeArrangement);

			if (activeArrangement.getStartDirection() == startDirection) {
				return arrangementInstance.forwardTrack;
			} else {
				Direction endDirection = activeArrangement.getEndDirection();

				if (endDirection != null && endDirection.reverse() == startDirection)
					return arrangementInstance.reverseTrack;
				else
					return null;
			}
		}
	}

	public void activateTrack(TrackArrangement arrangement, Runnable success) {
		switchState.activate(arrangement, success);
	}

	@Nullable
	public TrackArrangement getActiveTrack() {
		return switchState.getActiveArrangement();
	}

	@Override
	public String toString() {
		return String.format("FieldImpl(column = %s, row = %s)", column, row);
	}

	public LayoutImpl getLayout() {
		return layout;
	}

	private void updateCachedTrackData() {
		Map<TrackImpl, List<SignalImpl>> affectingSignalsByTrack = new HashMap<TrackImpl, List<SignalImpl>>();
		Map<SignalImpl, List<TrackImpl>> affectedTracksBySignal = new HashMap<SignalImpl, List<TrackImpl>>();
		Map<Direction, List<Track>> tracksWithStartDirection = new EnumMap<Direction, List<Track>>(Direction.class);

		for (Direction i : Direction.values()) {
			tracksWithStartDirection.put(i, new ArrayList<Track>());
		}

		List<TrackImpl> tracks = new ArrayList<TrackImpl>();

		for (TrackArrangementInstance i : trackArrangementInstances.values()) {
			tracks.add(i.forwardTrack);
			tracks.add(i.reverseTrack);
		}

		for (TrackImpl track : tracks) {
			affectingSignalsByTrack.put(track, new ArrayList<SignalImpl>());

			Edge startEdge = track.getStartEdge();

			if (startEdge != null)
				tracksWithStartDirection.get(startEdge.getDirection()).add(track);
		}

		for (SignalImpl signal : signalsByArrangement.values()) {
			List<TrackImpl> affectedTracks = new ArrayList<TrackImpl>();
			affectedTracksBySignal.put(signal, affectedTracks);

			for (TrackImpl track : tracks) {
				EdgeImpl startEdge = track.getStartEdge();
				EdgeImpl endEdge = track.getEndEdge();

				if (startEdge != null && endEdge != null && signal.getArrangement().affectsTrack(startEdge.getDirection(), endEdge.getDirection())) {
					affectedTracks.add(track);
					affectingSignalsByTrack.get(track).add(signal);
				}
			}
		}

		this.affectedTracksBySignal = affectedTracksBySignal;
		this.affectingSignalsByTrack = affectingSignalsByTrack;
		this.tracksWithStartDirection = tracksWithStartDirection;
	}

	public List<Track> getAffectedTracks(SignalImpl signal) {
		return Collections.<Track>unmodifiableList(affectedTracksBySignal.get(signal));
	}

	public List<SignalImpl> getAffectingSignals(TrackImpl track) {
		return Collections.unmodifiableList(affectingSignalsByTrack.get(track));
	}

	public TrackImpl getTrackForArrangement(TrackArrangement arrangement, boolean reversed) {
		TrackArrangementInstance arrangementInstance = trackArrangementInstances.get(arrangement);

		if (reversed)
			return arrangementInstance.reverseTrack;
		else
			return arrangementInstance.forwardTrack;
	}

	public void setColorForArrangement(TrackArrangement arrangements, Color color) {
		trackArrangementInstances.get(arrangements).color = color;
	}

	private abstract static class SwitchState {
		public abstract void activate(TrackArrangement arrangement, Runnable success);

		/**
		 * Return the active track, or null if none is active.
		 */
		@Nullable
		public abstract TrackArrangement getActiveArrangement();

		public void paint(Painter p) {
		}
	}

	private final class PassiveSwitchState extends SwitchState {
		private final TrackArrangement arrangement; // May be null

		private PassiveSwitchState(@Nullable TrackArrangement arrangement) {
			this.arrangement = arrangement;
		}

		@Override
		public void activate(TrackArrangement arrangement, Runnable success) {
			if (arrangement != this.arrangement)
				switchState = new ActivatingSwitchState(arrangement, success);
		}

		@Nullable
		@Override
		public TrackArrangement getActiveArrangement() {
			return arrangement;
		}

		@Override
		public void paint(Painter p) {
			if (arrangement != null && trackArrangementInstances.size() > 1)
				arrangement.paintActiveIndicator(p);
		}
	}

	private final class ActivatingSwitchState extends SwitchState {
		private final TrackArrangement arrangement;
		private final double startTime;

		private ActivatingSwitchState(final TrackArrangement arrangement, final Runnable success) {
			this.arrangement = arrangement;
			startTime = scheduler.getTime();

			scheduler.post(5, new Runnable() {
				@Override
				public void run() {
					switchState = new PassiveSwitchState(arrangement);
					success.run();
				}
			});
		}

		@Override
		public void activate(TrackArrangement arrangement, Runnable success) {
			throw new IllegalStateException();
		}

		@Override
		public void paint(Painter p) {
			if (MathUtil.mod(scheduler.getTime() - startTime, .4) < .2)
				arrangement.paintActiveIndicator(p);
		}

		@Override
		public TrackArrangement getActiveArrangement() {
			return null;
		}
	}
	
	private static final class TrackArrangementInstance {
		private final TrackImpl forwardTrack;
		private final TrackImpl reverseTrack;
		private Color color = Color.white;

		private TrackArrangementInstance(TrackImpl forwardTrack, TrackImpl reverseTrack) {
			this.forwardTrack = forwardTrack;
			this.reverseTrack = reverseTrack;
		}
	}
}
