package org.example.demoapi.api

import org.example.demoapi.utils.RangedList
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
object RangeBodyAdvice : ResponseBodyAdvice<List<Any?>> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>) =
            List::class.java.isAssignableFrom(returnType.parameterType)

    override fun beforeBodyWrite(
            body: List<Any?>?,
            returnType: MethodParameter,
            mediaType: MediaType,
            converterType: Class<out HttpMessageConverter<*>>,
            req: ServerHttpRequest,
            res: ServerHttpResponse): List<Any?>? {
        if (req.headers["Range"]?.any { it.startsWith("items=") } == true) {
            res.setStatusCode(HttpStatus.PARTIAL_CONTENT)
        }
        res.headers.add("Accept-Ranges", "items")
        if (body is RangedList) {
            res.headers.add(
                    "Content-Range",
                    "items ${body.start}-${body.start + body.size - 1}/${body.total ?: "*"}"
            )
        }
        return body
    }

}