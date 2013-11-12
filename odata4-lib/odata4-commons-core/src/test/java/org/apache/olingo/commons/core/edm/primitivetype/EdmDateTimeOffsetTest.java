package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmDateTimeOffsetTest extends PrimitiveTypeBaseTest {

  final EdmPrimitiveType instance = EdmPrimitiveTypeKind.DateTimeOffset.getEdmPrimitiveTypeInstance();

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("2009-12-26T21:23:38Z", instance.toUriLiteral("2009-12-26T21:23:38Z"));
    assertEquals("2002-10-10T12:00:00-05:00", instance.toUriLiteral("2002-10-10T12:00:00-05:00"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("2009-12-26T21:23:38Z", instance.fromUriLiteral("2009-12-26T21:23:38Z"));
    assertEquals("2002-10-10T12:00:00-05:00", instance.fromUriLiteral("2002-10-10T12:00:00-05:00"));
  }

  @Test
  public void valueToString() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    dateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(dateTime, null, null, null, null, null));
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(dateTime, null, null, 0, null, null));
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(dateTime, null, null, 5, null, null));

    dateTime.setTimeZone(TimeZone.getTimeZone("GMT-1:30"));
    assertEquals("2012-02-29T01:02:03-01:30", instance.valueToString(dateTime, null, null, null, null, null));

    dateTime.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));
    assertEquals("2012-02-29T01:02:03+11:00", instance.valueToString(dateTime, null, null, null, null, null));

    final Long millis = 1330558323007L;
    assertEquals("2012-02-29T23:32:03.007Z", instance.valueToString(millis, null, null, 3, null, null));
    assertEquals("1969-12-31T23:59:59.9Z", instance.valueToString(-100L, null, null, 1, null, null));
    assertEquals("1969-12-31T23:59:59.98Z", instance.valueToString(-20L, null, null, 2, null, null));

    final Date date = new Date(millis);
    final String time = date.toString().substring(11, 19);
    assertTrue(instance.valueToString(date, null, null, 3, null, null).contains(time));

    expectFacetsErrorInValueToString(instance, millis, null, null, null, null, null);
    expectFacetsErrorInValueToString(instance, 3L, null, null, 2, null, null);

    expectTypeErrorInValueToString(instance, 0);
  }

  @Test
  public void valueOfString() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    dateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals(dateTime, instance.valueOfString("2012-02-29T01:02:03Z", null, null, null, null, null,
        Calendar.class));
    assertEquals(Long.valueOf(dateTime.getTimeInMillis()), instance.valueOfString("2012-02-29T01:02:03+00:00", null,
        null, null, null, null, Long.class));
    assertEquals(dateTime, instance.valueOfString("2012-02-29T01:02:03", null, null, null, null, null,
        Calendar.class));

    dateTime.clear();
    dateTime.setTimeZone(TimeZone.getTimeZone("GMT-01:30"));
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals(dateTime.getTime(), instance.valueOfString("2012-02-29T01:02:03-01:30", null, null, null, null, null,
        Date.class));

    dateTime.clear();
    dateTime.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals(dateTime, instance.valueOfString("2012-02-29T01:02:03+11:00", null, null, null, null, null,
        Calendar.class));

    dateTime.add(Calendar.MILLISECOND, 7);
    assertEquals(dateTime, instance.valueOfString("2012-02-29T01:02:03.007+11:00", null, null, 3, null, null,
        Calendar.class));

    assertEquals(Long.valueOf(120000L), instance.valueOfString("1970-01-01T00:02", null, null, null, null, null,
        Long.class));
    assertEquals(Long.valueOf(12L), instance.valueOfString("1970-01-01T00:00:00.012", null, null, 3, null, null,
        Long.class));
    assertEquals(Long.valueOf(120L), instance.valueOfString("1970-01-01T00:00:00.12", null, null, 2, null, null,
        Long.class));

    expectFacetsErrorInValueOfString(instance, "2012-02-29T23:32:02.9Z", null, null, null, null, null);
    expectFacetsErrorInValueOfString(instance, "2012-02-29T23:32:02.9Z", null, null, 0, null, null);
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02X");
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02+24:00");
    expectContentErrorInValueOfString(instance, "2012-02-30T01:02:03");
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02.");
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02.0000000000000");

    expectTypeErrorInValueOfString(instance, "2012-02-29T01:02:03Z");
  }
}
