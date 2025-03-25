package com.example.fastfood.network;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class IntegerRequestBodyConverter implements Converter<Integer, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    @Override
    public RequestBody convert(Integer value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
    }
} 