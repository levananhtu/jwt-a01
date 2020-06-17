package lvat.protest.jwta01.payload;

import java.util.Map;

public class ArgumentsErrorPayload extends MessagePayload {
    private Map<String, String> argumentsErrorDetail;

    public ArgumentsErrorPayload(Integer httpStatusCode, Map<String, String> argumentsErrorDetail) {
//        super("Argument error", httpStatusCode);
        super("Argument error");
        this.argumentsErrorDetail = argumentsErrorDetail;
    }

    public Map<String, String> getArgumentsErrorDetail() {
        return argumentsErrorDetail;
    }

    public void setArgumentsErrorDetail(Map<String, String> argumentsErrorDetail) {
        this.argumentsErrorDetail = argumentsErrorDetail;
    }
}
