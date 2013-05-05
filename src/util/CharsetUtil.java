package util;

import java.nio.charset.Charset;

public class CharsetUtil {
	private CharsetUtil() {
	}
	
	public static final Charset utf8 = Charset.forName("utf-8");
	public static final Charset ascii = Charset.forName("ascii");
}
