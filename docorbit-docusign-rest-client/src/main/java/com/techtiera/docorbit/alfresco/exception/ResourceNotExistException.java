package com.techtiera.docorbit.alfresco.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceNotExistException extends DocorbitException{

  private static final long serialVersionUID = 1L;

  private String resourceId;

  private String resourceType;

  public ResourceNotExistException(final ErrorInfo errorInfo, final String resourceId, final String resourceType) {
    super(errorInfo);
    this.resourceId = resourceId;
    this.resourceType = resourceType;
  }

}
