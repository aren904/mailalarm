package cn.infocore.main;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.infocore.dto.MySnmpDTO;

public class SmnpNoticeTest {

	@Test
	public void test() {
		
		@SuppressWarnings("unused")
		MySnmpDTO mySnmp=MySnmpCache.getInstance().getMySnmp();

		assertTrue(true);
		}

}
