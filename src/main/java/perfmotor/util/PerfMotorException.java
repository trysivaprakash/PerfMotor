package perfmotor.util;

public class PerfMotorException extends Exception {

    private String msg;

    public PerfMotorException(String msg, Exception e) {
        super(e);
        this.msg = msg;
    }
}
