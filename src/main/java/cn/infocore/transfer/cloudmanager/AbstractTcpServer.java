package cn.infocore.transfer.cloudmanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Consumer;

import cn.infocore.transfer.ServerListener;

public class AbstractTcpServer<T> implements ServerListener {
	
	private Logger logger = LoggerFactory.getLogger(AbstractTcpServer.class);

	private Selector selector;

	private ServerSocketChannel servChannel;

	private volatile boolean exit;

	public AbstractTcpServer(int port) {
		try {
			selector = Selector.open();
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(false);
			servChannel.socket().bind(new InetSocketAddress(port), 1024);
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isExit() {
		return this.exit;
	}

	void doSelect() {
		try {
			selector.select(100);
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = selectedKeys.iterator();
			SelectionKey key = null;
			while (it.hasNext()) {
				key = it.next();
				it.remove();
				try {
					// handleInput(key);
				} catch (Exception e) {
					if (key != null) {
						key.cancel();
						if (key.channel() != null)
							key.channel().close();
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
    protected void handleInput(SelectionKey key ,Consumer handler) throws IOException {
		if (key.isValid()) {
		    // 处理新接入的请求消息
		    if (key.isAcceptable()) {
			// Accept the new connection
			ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
			SocketChannel sc = ssc.accept();
			sc.configureBlocking(false);
			// Add the new connection to the selector
			sc.register(selector, SelectionKey.OP_READ);
		    }
		    if (key.isReadable()) {
			// Read the data
			SocketChannel sc = (SocketChannel) key.channel();
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			int readBytes = sc.read(readBuffer);
			
			if (readBytes > 0) {
			    readBuffer.flip();
			    byte[] bytes = new byte[readBuffer.remaining()];
			    readBuffer.get(bytes);
			    String body = new String(bytes, "UTF-8");
			    System.out.println("The time server receive order : "
				    + body);
			    String currentTime = "QUERY TIME ORDER"
				    .equalsIgnoreCase(body) ? new java.util.Date(
				    System.currentTimeMillis()).toString()
				    : "BAD ORDER";
			    doWrite(sc, currentTime);
			} else if (readBytes < 0) {
			    // 对端链路关闭
			    key.cancel();
			    sc.close();
			} else
			    ; // 读到0字节，忽略
		    }
		}
    }

    private void doWrite(SocketChannel channel, String response)
	    throws IOException {
		if (response != null && response.trim().length() > 0) {
		    byte[] bytes = response.getBytes();
		    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		    writeBuffer.put(bytes);
		    writeBuffer.flip();
		    channel.write(writeBuffer);
		}
    }
	
	void doExit() {
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean select() {
		doSelect();
		return false;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}

}
