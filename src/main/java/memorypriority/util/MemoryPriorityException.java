package memorypriority.util;

public class MemoryPriorityException extends RuntimeException {

    public MemoryPriorityException() {
    }

    public MemoryPriorityException(String msg) {
        super(msg);
    }

    public MemoryPriorityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

