package org.example.demoapi.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.http.MediaType
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter

object YamlHttpMessageConverter : AbstractJackson2HttpMessageConverter(
        ObjectMapper(YAMLFactory())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(JavaTimeModule()),
        MediaType("text", "yaml"),
        MediaType("text", "*+yaml")
)