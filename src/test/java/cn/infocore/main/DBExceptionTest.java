package cn.infocore.main;

import static org.junit.Assert.*;

import org.junit.Test;

public class DBExceptionTest {

	@Test
	public void test() {
		
		ThreadScanStreamer.updateOffLine("0f8e4faa-cff2-4e33-9183-c088ae1da2e1", true);
		assertTrue(true);
	}

}
