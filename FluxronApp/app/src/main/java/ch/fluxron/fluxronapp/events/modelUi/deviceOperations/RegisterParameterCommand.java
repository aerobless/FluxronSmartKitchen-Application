package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to register interest into a specific parameter.
 */
public class RegisterParameterCommand extends RequestResponseConnection {
    String parameter;

    public RegisterParameterCommand(String parameter) {
        setParameter(parameter);
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        if(parameter != null){
            this.parameter = parameter;
        } else{
            throw new IllegalArgumentException("Null is not allowed.");
        }
    }
}
