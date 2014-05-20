package vp.ipolly.service;


/**
 * 
 * @author vp.song
 * 
 */
public interface Connector {
	
	
}
//	private Logger logger = Logger.getLogger("CLIENT");
//
//	private SocketChannel socketChannel;
//	private final InetSocketAddress SERVER_ADDRESS;
//	private ByteBuffer buffer = ByteBuffer.allocate(40000);
//	private Selector selector;
//	private Handler handler;
//	private boolean stop;
//
//	public Connector(InetSocketAddress address, Handler handler) {
//		this.SERVER_ADDRESS = address;
//		this.handler = handler;
//	}
//
//	public void connect() {
//		try {
//			socketChannel = SocketChannel.open();
//			socketChannel.configureBlocking(false);
//			selector = Selector.open();
//			socketChannel.register(selector, SelectionKey.OP_CONNECT
//					| SelectionKey.OP_READ);
//			socketChannel.connect(SERVER_ADDRESS);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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
//		if (selectionKey.isConnectable()) {
//			try {
//				if (socketChannel.isConnectionPending()) {
//					socketChannel.finishConnect();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			handler.connected();
//		}
//		if (selectionKey.isReadable()) {
//			try {
//				socketChannel.read(buffer);
//				handler.received(new String(buffer.array()));
//			} catch (IOException e) {
//				try {
//					socketChannel.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				e.printStackTrace();
//				stop();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public synchronized void stop() {
//		this.stop = true;
//		logger.info("client has stopped");
//	}
//
//}
