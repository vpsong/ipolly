package vp.ipolly.protocol;

public interface Decoder<T> {

	T decode(byte[] bytes);
}
