package com.tencent.bk.devops.atom.task

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtils {
    val objectMapper = jacksonObjectMapper().apply {
        //        val javaTimeModule = JavaTimeModule()
//
//        javaTimeModule.addSerializer(LocalTime::class.java, LocalTimeSerializer(DateTimeFormatter.ISO_TIME))
//        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ISO_DATE))
//        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
//        javaTimeModule.addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateTimeFormatter.ISO_TIME))
//        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ISO_DATE))
//        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME))
//        registerModule(javaTimeModule)
//        registerModule(ParameterNamesModule())
//        registerModule(Jdk8Module())

        configure(SerializationFeature.INDENT_OUTPUT, true)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    }
}