package com.hgtech.sageoriapp;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;

public class GsonDateFormatAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

  private final DateFormat dateFormat;

  public GsonDateFormatAdapter() {
    dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.KOREA);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(dateFormat.format(date));
  }

  @Override
  public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
    try {
      return dateFormat.parse(jsonElement.getAsString());
    } catch (ParseException e) {
      throw new JsonParseException(e);
    }
  }
}
