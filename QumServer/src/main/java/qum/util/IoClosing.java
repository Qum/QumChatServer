package qum.util;

public class IoClosing {
    public static void silentClose(AutoCloseable... Streams) {
	for (AutoCloseable ac : Streams) {
	    if (ac == null) {
		return;
	    }

	    try {
		ac.close();
		System.err.println("silentClose success");
	    } catch (Exception e) {
		System.err.println("silentClose has problem");
		e.printStackTrace();
	    }
	}
    }
}
