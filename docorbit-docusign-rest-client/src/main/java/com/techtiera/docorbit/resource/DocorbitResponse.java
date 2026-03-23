package com.techtiera.docorbit.resource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocorbitResponse {

  private String status;

  private Object data;

  private ErrorData error;

}
