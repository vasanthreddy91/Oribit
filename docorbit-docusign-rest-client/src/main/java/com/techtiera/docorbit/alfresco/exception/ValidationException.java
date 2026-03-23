package com.techtiera.docorbit.alfresco.exception;

/**
 * An instance of this class is thrown when the parsing is not possible.
 *
 * @version 1.0.0
 */
public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = -1778831912040748858L;

  public ValidationException(final String message) {
    super(message);
  }
}
