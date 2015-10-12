package ch.fluxron.fluxronapp.events.base;

import java.util.UUID;

/**
 * Base class for connections following the request-response pattern via an EventBus
 */
public class RequestResponseConnection {
    private String connectionId;

    /**
     * Creates a new instance of RequestResponseConnection. Use getConnectionId() after the initialization
     * of this class to get the identifier of the connection
     */
    public RequestResponseConnection(){
        connectionId = UUID.randomUUID().toString();
    }

    /**
     * Returns the connection id. Use this to test if a reply event is answering your request.
     * @return
     */
    public String getConnectionId(){
        return connectionId;
    }

    /**
     * Set the connection id. Only use this to reply to an existing connection.
     */
    public void setConnectionId(RequestResponseConnection original){
        this.connectionId = original.connectionId;
    }
}
