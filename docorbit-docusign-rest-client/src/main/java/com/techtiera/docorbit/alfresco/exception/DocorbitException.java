package com.techtiera.docorbit.alfresco.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocorbitException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 4835737202341834322L;

  private final ErrorInfo errorInfo;

  public DocorbitException(final ErrorInfo errorInfo) {
    this.errorInfo = errorInfo;
  }

}
