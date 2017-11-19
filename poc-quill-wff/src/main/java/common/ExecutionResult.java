package common;

import java.util.Map;

/**
 * Created by admin on 18/04/17.
 */
public class ExecutionResult {
    public int code;
    public String strStdout;
    Map<String, String> env;

    public ExecutionResult(int code, String strStdout, Map<String, String> env) {
        this.code = code;
        this.strStdout = strStdout;
        this.env = env;
    }
}
