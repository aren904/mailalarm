package cn.infocore.main;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.infocore.entity.MySnmp;

public class SmnpNoticeTest {

	@Test
	public void test() {
		
		@SuppressWarnings("unused")
		MySnmp mySnmp=MySnmpCache.getInstance().getMySnmp();

		assertTrue(true);
		}

}
