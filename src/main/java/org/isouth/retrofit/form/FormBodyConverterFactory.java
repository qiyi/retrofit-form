package org.isouth.retrofit.form;

import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FormBodyConverterFactory extends Converter.Factory {
    public Converter<?, RequestBody> requestBodyConverter(
            Type type, Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations, Retrofit retrofit) {
        return type instanceof Class<?> ? Arrays.stream(parameterAnnotations)
                .filter(a -> a instanceof FormBody)
                .findAny()
                .map(a -> new FormBodyConverter<>(retrofit, (Class<?>) type))
                .orElse(null) : null;
    }

    public class FormBodyConverter<T> implements Converter<T, RequestBody> {
        private final Retrofit retrofit;
        private final Field[] fields;
        private final Map<String, String> fieldKeys = new HashMap<>();

        public FormBodyConverter(Retrofit retrofit, Class<?> type) {
            this.retrofit = retrofit;
            this.fields = type.getDeclaredFields();
            for (Field field : this.fields) {
                field.setAccessible(true);
                FormField formField = field.getAnnotation(FormField.class);
                if (formField != null) {
                    fieldKeys.put(field.getName(), formField.value());
                }
            }
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
            for (Field field : fields) {
                Object fieldValue;
                try {
                    fieldValue = field.get(value);
                } catch (IllegalAccessException e) {
                    throw new IOException("Get field value failed.", e);
                }
                if (fieldValue != null) {
                    String fieldKey = fieldKeys.getOrDefault(field.getName(), field.getName());
                    builder.add(fieldKey, String.valueOf(fieldValue));
                }
            }
            return builder.build();
        }
    }
}
