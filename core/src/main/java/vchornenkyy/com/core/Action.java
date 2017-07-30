package vchornenkyy.com.core;

public  interface Action<T> {
    void call(T data);
}
