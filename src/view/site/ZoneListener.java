package view.site;

public abstract class ZoneListener {
	public void mouseEntered() { }
	public void mouseExited() { }
	public void mouseDown() { }
	public void mouseUp() { }

	//public enum MouseState {
	//	outside, hovering, down, dragging
	//}

	public static final class BasicHovering extends ZoneListener {
		public boolean active = false;

		@Override
		public void mouseEntered() {
			active = true;
		}

		@Override
		public void mouseExited() {
			active = false;
		}
	}
}
