import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

/** Simple toy program illustrating how to run Luaj in multiple threads. 
 * 
 * By creating separate Globals in each thread, scripts can be run in each thread.
 * 
 * However note the following:
 *  - type-related metatables such as LuaNumber.s_metatable are shared by all threads, so are not thread-safe.
 *  - creating additional threads within a Java element called by lua could result in shared access to globals.
 */
public class SampleMultiThreaded {

	static class Runner implements Runnable {
		final String script1, script2;
		Runner(String script1, String script2) {
			this.script1 = script1;
			this.script2 = script2;
		}
		public void run() {
			try {
				// Each thread must have its own Globals.
				Globals g = JsePlatform.standardGlobals();

				// Once a Globals is created, it can and should be reused 
				// within the same thread.
				g.loadFile(script1).call();
				g.loadFile(script2).call();

			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void main(final String[] args) throws IOException {
		final String script1 = args.length > 0? args[0]: "test/lua/perf/nsieve.lua";
		final String script2 = args.length > 1? args[1]: "test/lua/perf/binarytrees.lua";
		try {
			Thread[] thread = new Thread[10];
			for (int i = 0; i < thread.length; ++i)
				thread[i] = new Thread(new Runner(script1, script2),"Runner-"+i);
			for (int i = 0; i < thread.length; ++i)
				thread[i].start();
			for (int i = 0; i < thread.length; ++i)
				thread[i].join();
			System.out.println("done");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
