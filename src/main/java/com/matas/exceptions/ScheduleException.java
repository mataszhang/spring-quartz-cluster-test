package com.matas.exceptions;

import com.dexcoder.commons.exceptions.DexcoderException;

 
public class ScheduleException extends DexcoderException {

    /** serialVersionUID */
    private static final long serialVersionUID = -1921648378954132894L;

    /**
     * Instantiates a new ScheduleException.
     *
     * @param e the e
     */
    public ScheduleException(Throwable e) {
        super(e);
    }

    /**
     * Constructor
     *
     * @param message the message
     */
    public ScheduleException(String message) {
        super(message);
    }
    

    /**
     * Constructor
     *
     * @param message the message
     */
    public ScheduleException(String message,Throwable e) {
        super(message,e);
    }

    /**
     * Constructor
     *
     * @param code the code
     * @param message the message
     */
    public ScheduleException(String code, String message) {
        super(code, message);
    }
}
