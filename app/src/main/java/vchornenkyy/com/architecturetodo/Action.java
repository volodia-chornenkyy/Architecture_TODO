package vchornenkyy.com.architecturetodo;

public  interface Action<T> {
    void call(T data);
}
