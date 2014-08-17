package hardware;

public class Rfm12bTimeoutException extends RuntimeException {
                                   public Rfm12bTimeoutException(long start, long timeout){
                                                                                         super("Timeout at "+System.currentTimeMillis() + ". Started at "+start+". Waited "+timeout+".");
                                   }
}
