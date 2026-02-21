package com.dnd.modutime.documentation;

import com.dnd.modutime.advice.GlobalControllerAdvice;
import com.dnd.modutime.common.DisplayableEnum;
import com.dnd.modutime.common.convert.DisplayableEnumJsonConverter;
import com.dnd.modutime.util.DateTimeConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

public class MockMvcFactory {

    public static final String APP_DEV_DNS = "api2.modutime.site";
    public static final ObjectMapper MOCK_MVC_MAPPER;
    public static final String HEADER_AUTHORIZATION = "Bearer JWT-TOKEN";

    static {
        MOCK_MVC_MAPPER = new ObjectMapper();

        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        javaTimeModule.addSerializer(ZonedDateTime.class,
                new ZonedDateTimeSerializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        javaTimeModule.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);

        var customModule = new SimpleModule()
                .addSerializer(DisplayableEnum.class, new DisplayableEnumJsonConverter.Serializer())
                .addDeserializer(Enum.class, new DisplayableEnumJsonConverter.Deserializer());

        MOCK_MVC_MAPPER.registerModules(
                javaTimeModule,
                customModule
        );

        MOCK_MVC_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MOCK_MVC_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static MockMvc getMockMvc(Object... controllers) {
        return getMockMvcBuilder(controllers).build();
    }

    public static MockMvc getRestDocsMockMvc(RestDocumentationContextProvider restDocumentationContextProvider,
                                             Object... controllers) {
        return getRestDocsMockMvc(restDocumentationContextProvider, APP_DEV_DNS, controllers);
    }

    public static MockMvc getRestDocsMockMvc(RestDocumentationContextProvider restDocumentationContextProvider,
                                             String host,
                                             Object... controllers) {
        var documentationConfigurer = documentationConfiguration(restDocumentationContextProvider);
        documentationConfigurer.uris().withScheme("https").withHost(host).withPort(443);
        return getMockMvcBuilder(controllers).apply(documentationConfigurer).build();
    }

    public static MockMvc getRestDocsMockMvc(RestDocumentationContextProvider restDocumentationContextProvider,
                                             String host,
                                             HandlerMethodArgumentResolver[] argumentResolvers,
                                             Object... controllers) {
        var documentationConfigurer = documentationConfiguration(restDocumentationContextProvider);
        documentationConfigurer.uris().withScheme("https").withHost(host).withPort(443);
        return getMockMvcBuilder(controllers)
                .setCustomArgumentResolvers(argumentResolvers)
                .apply(documentationConfigurer)
                .build();
    }

    private static StandaloneMockMvcBuilder getMockMvcBuilder(Object... controllers) {
        var conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new LocalDateTimeConverter());
        conversionService.addConverter(new LocalDateConverter());
        conversionService.addConverter(new LocalTimeConverter());

        return MockMvcBuilders.standaloneSetup(controllers)
                .setControllerAdvice(
                        new GlobalControllerAdvice()
                )
                .setConversionService(conversionService)
                .setMessageConverters(
                        new StringHttpMessageConverter(StandardCharsets.UTF_8),
                        new MappingJackson2HttpMessageConverter(MOCK_MVC_MAPPER))
                ;
    }

    public static class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(DateTimeConstants.FORMAT_DEFAULT_DATE_TIME));
        }
    }

    public static class LocalDateConverter implements Converter<String, LocalDate> {

        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern(DateTimeConstants.FORMAT_DATE));
        }
    }

    public static class LocalTimeConverter implements Converter<String, LocalTime> {
        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source, DateTimeFormatter.ofPattern(DateTimeConstants.FORMAT_TIME));
        }
    }
}
