package org.apache.olingo.commons.core.edm.primitivetype;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Double.
 */
final class EdmDouble extends SingletonPrimitiveType {

  protected static final String NEGATIVE_INFINITY = "-INF";
  protected static final String POSITIVE_INFINITY = "INF";
  protected static final String NaN = "NaN";
  private static final Pattern PATTERN = Pattern.compile(
      "(?:\\+|-)?\\p{Digit}{1,17}(?:\\.\\p{Digit}{1,17})?(?:(?:E|e)(?:\\+|-)?\\p{Digit}{1,3})?");
  private static final EdmDouble instance = new EdmDouble();

  public static EdmDouble getInstance() {
    return instance;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof Uint7
        || primitiveType instanceof EdmByte
        || primitiveType instanceof EdmSByte
        || primitiveType instanceof EdmInt16
        || primitiveType instanceof EdmInt32
        || primitiveType instanceof EdmInt64
        || primitiveType instanceof EdmSingle
        || primitiveType instanceof EdmDouble;
  }

  @Override
  public Class<?> getDefaultType() {
    return Double.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    Double result = null;
    BigDecimal bigDecimalValue = null;
    // Handle special values first.
    if (value.equals(NEGATIVE_INFINITY)) {
      result = Double.NEGATIVE_INFINITY;
    } else if (value.equals(POSITIVE_INFINITY)) {
      result = Double.POSITIVE_INFINITY;
    } else if (value.equals(NaN)) {
      result = Double.NaN;
    } else {
      // Now only "normal" numbers remain.
      if (!PATTERN.matcher(value).matches()) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
      }

      // The number format is checked above, so we don't have to catch NumberFormatException.
      bigDecimalValue = new BigDecimal(value);
      result = bigDecimalValue.doubleValue();
      // "Real" infinite values have been treated already above, so we can throw an exception
      // if the conversion to a double results in an infinite value.
      if (result.isInfinite() || BigDecimal.valueOf(result).compareTo(bigDecimalValue) != 0) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
      }
    }

    if (returnType.isAssignableFrom(Double.class)) {
      return returnType.cast(result);
    } else if (result.isInfinite() || result.isNaN()) {
      if (returnType.isAssignableFrom(Float.class)) {
        return returnType.cast(result.floatValue());
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType)");
      }
    } else {
      try {
        return EdmDecimal.convertDecimal(bigDecimalValue, returnType);
      } catch (final IllegalArgumentException e) {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType), e");
      } catch (final ClassCastException e) {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
      }
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Long) {
      if (Math.abs((Long) value) < 1L << 51) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
      return value.toString();
    } else if (value instanceof Double) {
      return (Double) value == Double.NEGATIVE_INFINITY ? NEGATIVE_INFINITY :
          (Double) value == Double.POSITIVE_INFINITY ? POSITIVE_INFINITY : value.toString();
    } else if (value instanceof Float) {
      return (Float) value == Float.NEGATIVE_INFINITY ? NEGATIVE_INFINITY :
          (Float) value == Float.POSITIVE_INFINITY ? POSITIVE_INFINITY : value.toString();
    } else if (value instanceof BigDecimal) {
      final double doubleValue = ((BigDecimal) value).doubleValue();
      if (!Double.isInfinite(doubleValue) && BigDecimal.valueOf(doubleValue).compareTo((BigDecimal) value) == 0) {
        return ((BigDecimal) value).toString();
      } else {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }
}
