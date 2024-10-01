package com.crmws.worker.constant;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public enum ParameterDataType {

	  DECIMAL(Double.class) {
	    @Override
	    public Object parse(Object input) {
	      return Double.parseDouble(validate(input));
	    }
	  },
	  INTEGER(Integer.class) {
	    @Override
	    public Object parse(Object input) {
	      validate(input);
	      return Integer.parseInt(validate(input));
	    }
	  },
	  DATE(Date.class) {
	    @Override
	    public Object parse(Object input) {
	      validate(input);
	      return parseDateFormats(validate(input)).toEpochMilli();
	    }
	  },
	  BOOLEAN(Boolean.class) {
	    @Override
	    public Object parse(Object input) {
	      validate(input);
	      return Boolean.valueOf(validate(input));
	    }
	  },
	  LONG(Long.class) {
	    @Override
	    public Object parse(Object input) {
	      validate(input);
	      return Long.parseLong(validate(input));
	    }
	  },
	  TEXT(String.class) {
	    @Override
	    public Object parse(Object input) {
	      return validate(input);
	    }
	  },
	  COMPLEX(Object.class) {
	    @Override
	    public Object parse(Object input) {
	      return input;
	    }
	  };

	  private final Class aClass;

	  ParameterDataType(Class aClass) {
	    this.aClass = aClass;
	  }

	  private static String validate(Object input) {
	    Objects.requireNonNull(input, Fields.NULL_MESSAGE);
	    return String.valueOf(input);
	  }

	  public static Instant parseDateFormats(String candidate) {
	    if (candidate.trim().equalsIgnoreCase(Fields.NOW)) {
	      return Instant.now();
	    }
	    return Instant.parse(candidate);
	  }

	  public Class getaClass() {
	    return aClass;
	  }

	  public abstract Object parse(Object input);

	  public static class Fields {

	    public static final String NULL_MESSAGE = "Must not be null";

	    public static final String NOW = "NOW";

	    private Fields() {}
	  }


}
