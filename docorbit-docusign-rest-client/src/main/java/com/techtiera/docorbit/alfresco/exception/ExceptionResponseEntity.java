package com.techtiera.docorbit.alfresco.exception;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExceptionResponseEntity extends RuntimeException implements Serializable {

  private static final long serialVersionUID = 6210985214828895610L;
  private String message;
  private String code;
  private String exceptionType;
  private String uri;

}
