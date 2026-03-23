package com.techtiera.docorbit.alfresco.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorInfoDynamic {

  private String errorKey;

  private String errorMessage;

  private String errorDescription;

}
