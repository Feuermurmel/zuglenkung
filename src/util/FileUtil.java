package util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class FileUtil {
	private static final int tempDirAttempts = 10000;

	private FileUtil() {
	}
	
	// Stolen and adapted from http://code.google.com/p/guava-libraries/
	/**
	 * Atomically creates a new directory somewhere beneath the system's
	 * temporary directory (as defined by the {@code java.io.tmpdir} system
	 * property), and returns its name.
	 * <p/>
	 * <p>Use this method instead of {@link File#createTempFile(String, String)}
	 * when you wish to create a directory, not a regular file.  A common pitfall
	 * is to call {@code createTempFile}, delete the file and create a
	 * directory in its place, but this leads a race condition which can be
	 * exploited to create security vulnerabilities, especially when executable
	 * files are to be written into the directory.
	 * <p/>
	 * <p>This method assumes that the temporary volume is writable, has free
	 * inodes and free blocks, and that it will not be called thousands of times
	 * per second.
	 *
	 * @return the newly-created directory
	 * @throws IllegalStateException if the directory could not be created
	 */
	public static File createTempDir() {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));

		for (int counter = 0; counter < tempDirAttempts; counter += 1) {
			File tempDir = new File(baseDir, RandomUtil.sample(StringUtil.lowercaseLetters + StringUtil.digits, 20));

			if (tempDir.mkdir())
				return tempDir;
		}

		throw new IllegalStateException("Failed to create directory");
	}
	
	@SuppressWarnings("ConstantConditions")
	public static void deleteTree(File f) throws IOException {
		if (f.isDirectory())
			for (File c : f.listFiles())
				deleteTree(c);
		
		if (!f.delete())
			if (f.exists())
				throw new IOException("Failed to delete file: " + f);
	}

	public static void writeFile(File destination, String content) throws IOException {
		writeFile(destination, content.getBytes(CharsetUtil.utf8));
	}

	// FIXME: Should fail if the file already exists.
	public static void writeFile(File destination, byte[] content) throws IOException {
		File parent = destination.getParentFile();
		
		if (!parent.mkdirs())
			if (!parent.isDirectory())
				throw new RuntimeException("File.mkdirs() failed.");
		
		FileOutputStream outputStream = new FileOutputStream(destination);
		
		try {
			WritableByteChannel outputChannel = outputStream.getChannel();

			outputChannel.write(ByteBuffer.wrap(content));
		} finally {
			outputStream.close();
		}
	}
	
	public static byte[] readFileBytes(File source) throws IOException {
		FileInputStream inputStream = new FileInputStream(source);
		
		try {
			ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
			ReadableByteChannel inputChannel = inputStream.getChannel();
			WritableByteChannel byteArrayChannel = Channels.newChannel(byteArrayStream);
			ByteBuffer buffer = ByteBuffer.allocate(1 << 12);

			while (true) {
				int res = inputChannel.read(buffer);
				
				if (res == -1)
					break;
				
				buffer.flip();
				byteArrayChannel.write(buffer);
				buffer.rewind();
			}
			
			return byteArrayStream.toByteArray();
		} finally {
			inputStream.close();
		}
	}
	
	public static String readFile(File source) throws IOException {
		return new String(readFileBytes(source), CharsetUtil.utf8);
	}
}
