package lvat.protest.jwta01.payload;

public class MessagePayload implements PayloadResponse {
    private String message;
//    private Integer httpStatusCode;

    public MessagePayload() {
    }

//    public MessagePayload(String message, Integer httpStatusCode) {
//        this.message = message;
//        this.httpStatusCode = httpStatusCode;
//    }

    public MessagePayload(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public Integer getHttpStatusCode() {
//        return httpStatusCode;
//    }
//
//    public void setHttpStatusCode(Integer httpStatusCode) {
//        this.httpStatusCode = httpStatusCode;
//    }
}
