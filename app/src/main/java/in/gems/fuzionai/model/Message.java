package in.gems.fuzionai.model;

public class Message {
    private String message;
    private String sender;
    private String messageType;

    // Default constructor
    public Message()
    {   }

    // Parameterized constructor
    public Message(String message, String sender) {
        this.message = message;
        this.sender = sender;
        this.messageType = sender.equals("Pixel") ? "image" : "text";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
