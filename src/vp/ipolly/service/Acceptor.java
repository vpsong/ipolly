package vp.ipolly.service;


/**
 * 
 * @author vp.song
 * 
 */
public interface Acceptor {
	
}



//implements Runnable {
//	private Logger logger = Logger.getLogger("SERVER");
//
//	private Selector selector;
//	private ServerSocketChannel serverSocketChannel;
//	private Handler handler;
//	private boolean stop;
//
//	public Acceptor(int port) {
//		try {
//			selector = Selector.open();
//			serverSocketChannel = ServerSocketChannel.open();
//			serverSocketChannel.socket().bind(new InetSocketAddress(port));
//			serverSocketChannel.configureBlocking(false);
//			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		logger.info("server has started up");
//	}
//
//	@Override
//	public void run() {
//		while (!stop) {
//			try {
//				selector.select();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Set<SelectionKey> selectionKeys = selector.selectedKeys();
//			for (SelectionKey selectionKey : selectionKeys) {
//				handle(selectionKey);
//			}
//			selectionKeys.clear();
//		}
//	}
//
//	private void handle(SelectionKey selectionKey) {
//		SocketChannel socketChannel = null;
//		if (selectionKey.isAcceptable()) {
//			try {
//				socketChannel = serverSocketChannel.accept();
//				socketChannel.configureBlocking(false);
//				socketChannel.register(selector, SelectionKey.OP_READ,
//						ByteBuffer.allocate(4000));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			handler.accepted();
//		}
//		if (selectionKey.isReadable()) {
//			try {
//				ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
//				socketChannel = (SocketChannel) selectionKey
//						.channel();
//				socketChannel.read(buffer);
//				handler.received(new String(buffer.array()));
//			} catch (IOException e) {
//				try {
//					socketChannel.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public synchronized void stop() {
//		this.stop = true;
//		logger.info("server has stopped");
//	}
//
//}
