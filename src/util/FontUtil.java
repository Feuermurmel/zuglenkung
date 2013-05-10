package util;

import java.awt.Font;

public class FontUtil {
	private FontUtil() {
	}

	//	public static final Font helvetica = font();

	@SuppressWarnings("MagicConstant")
	public static Font font(String name, int size, Style style) {
		return new Font(name, style.magic, size);
	}

	public static Font font(String name, int size) {
		return font(name, size, Style.plain);
	}

	public enum Style {
		plain(Font.PLAIN), bold(Font.BOLD), italic(Font.ITALIC), boldItalic(Font.BOLD | Font.ITALIC);

		public final int magic;

		Style(int magic) {
			this.magic = magic;
		}
	}
}
