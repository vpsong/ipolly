package vp.ipolly.protocol;

public interface Encoder<T> {

	byte[] encode(T t);
}
