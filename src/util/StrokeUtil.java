package util;

import java.awt.BasicStroke;
import java.awt.Stroke;

public class StrokeUtil {
	private StrokeUtil() {
	}

	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	public static Stroke basic(float width, Cap cap, Join join, float[] pattern) {
		return new BasicStroke(width, cap.magic, join.magic, 10, pattern, 0f);
	}
	
	public static Stroke basic(float width, Cap cap, Join join, float pattern) {
		return basic(width, cap, join, new float[] { pattern, pattern });
	}

	public static Stroke basic(float width, Cap cap, Join join) {
		return basic(width, cap, join, null);
	}

	public static Stroke basic(float width, float[] pattern) {
		return basic(width, Cap.butt, Join.round, pattern);
	}

	public static Stroke basic(float width, float pattern) {
		return basic(width, Cap.butt, Join.round, new float[] { pattern, pattern });
	}

	public static Stroke basic(float width) {
		return basic(width, null);
	}

	public enum Cap {
		butt(BasicStroke.CAP_BUTT), round(BasicStroke.CAP_ROUND), square(BasicStroke.CAP_SQUARE);

		private final int magic;

		Cap(int magic) {
			this.magic = magic;
		}
	}

	public enum Join {
		bevel(BasicStroke.JOIN_BEVEL), miter(BasicStroke.JOIN_MITER), round(BasicStroke.JOIN_ROUND);

		private final int magic;

		Join(int magic) {
			this.magic = magic;
		}
	}
}
