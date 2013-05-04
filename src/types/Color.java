package types;

import java.awt.PaintContext;

import util.MathUtil;

public final class Color {
	public final float r;
	public final float g;
	public final float b;
	public PaintContext paintContext = null;

	private Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color add(Color t) {
		return create(r + t.r, g + t.g, b + t.b);
	}

	//public RGBTriple sub(RGBTriple t) {
	//	return new RGBTriple(red - t.red, green - t.green, blue - t.blue);
	//}

	public Color darken(float s) {
		return blend(black, s);
	}

	public Color brighten(float s) {
		return blend(white, s);
	}
	
	@Deprecated
	public Color scale(float s) {
		return darken(1 - s);
	}
	
	/** Values between 0 and 1 make the color whiter. */
	@Deprecated
	public Color shine(float s) {
		return brighten(s);
	}

	public Color mul(Color c) {
		return create(r * c.r, g * c.g, b * c.b);
	}

	@Override
	public String toString() {
		return String.format("Color(%f, %f, %f)", r, g, b);
	}
	
	public Color blend(Color other, float s) {
		return create(MathUtil.blend(r, other.r, s), MathUtil.blend(g, other.g, s), MathUtil.blend(b, other.b, s));
	}
	
	public java.awt.Color asAWTColor() {
		return new java.awt.Color(r, g, b);
	}

	public static Color gray(float b) {
		return create(b, b, b);
	}

	public static Color create(float r, float g, float b) {
		return new Color(r, g, b);
	}

	public static final Color black = create(0f, 0f, 0f);
	public static final Color blue = create(0f, 0f, 1f);
	public static final Color green = create(0f, 1f, 0f);
	public static final Color cyan = create(0f, 1f, 1f);
	public static final Color red = create(1f, 0f, 0f);
	public static final Color magenta = create(1f, 0f, 1f);
	public static final Color yellow = create(1f, 1f, 0f);
	public static final Color white = create(1f, 1f, 1f);
}
