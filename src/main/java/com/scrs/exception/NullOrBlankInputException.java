package com.scrs.exception;

public class NullOrBlankInputException
    extends RuntimeException{
        public NullOrBlankInputException(String fieldName){
            super(fieldName+ " Cannot be a Null or Blank .");
    }
}
