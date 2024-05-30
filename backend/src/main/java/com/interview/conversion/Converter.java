package com.interview.conversion;

public interface Converter<I, O> {

  O forward(I input);

  I backward(O input);
}
