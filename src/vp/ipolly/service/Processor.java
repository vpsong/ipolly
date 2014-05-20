package vp.ipolly.service;

/**
 * 
 * @author vp.song
 * 
 */
public interface Processor {

	void doSelect();
	
	void addNew(Session session);
	
	void remove(Session session);
	
	void startup();
	
	void regWriteOps(Session session);
}

// implements Runnable {
//
// private Selector selector;
// private boolean stop;
//
// public Processor() {
// try {
// selector = Selector.open();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
//
// @Override
// public void run() {
// while(!stop) {
// try {
// selector.select();
// } catch (IOException e) {
// e.printStackTrace();
// }
// Set<SelectionKey> selectionKeys = selector.selectedKeys();
// for (SelectionKey selectionKey : selectionKeys) {
// handle(selectionKey);
// }
// selectionKeys.clear();
// }
// }
//
// private void handle(SelectionKey selectionKey) {
//
// }
//
// public void stop() {
// this.stop = true;
// }
// }
