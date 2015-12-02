package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to register interest into a specific parameter.
 */
public class RegisterParameterCommand extends RequestResponseConnection {
    String parameter;

    /**
     * Instantiates a new RegisterParameterCommand.
     *
     * @param parameter
     */
    public RegisterParameterCommand(String parameter) {
        setParameter(parameter);
    }

    /**
     * Returns the parameter that should be registered.
     *
     * @return parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Sets the parameter that should be registered. Throws a IllegalArgumentException if the
     * supplied parameter is null.
     *
     * @param parameter
     */
    public void setParameter(String parameter) {
        if (parameter != null) {
            this.parameter = parameter;
        } else {
            throw new IllegalArgumentException("Null is not allowed.");
        }
    }
}
