package pl.iodkovskaya.leaveRequestSystem.exception;

public class RoleExistException extends RuntimeException{
    public RoleExistException(String message) {
        super(message);
    }
}
