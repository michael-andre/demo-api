package org.example.demoapi.api

import org.example.demoapi.utils.EndOffsetRange
import org.example.demoapi.utils.OffsetRange
import org.example.demoapi.utils.Range
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

object RangeResolver : HandlerMethodArgumentResolver {

    private val headerFormat = Regex("items=(\\d+)?-?(\\d+)?")
    private val paramFormat = Regex("(\\d+)?-?(\\d+)?")

    override fun supportsParameter(parameter: MethodParameter) =
            parameter.parameterType.isAssignableFrom(Range::class.java)

    override fun resolveArgument(
            parameter: MethodParameter,
            mvc: ModelAndViewContainer?,
            req: NativeWebRequest,
            binderFactory: WebDataBinderFactory?
    ): Range? {
        var range = parseRange(req)
        val annotation = parameter.getParameterAnnotation(RequestRange::class.java)
        if (annotation != null) {
            if (annotation.defaultSize > 0) {
                if (range == null) {
                    range = OffsetRange(0, annotation.defaultSize - 1)
                } else if (range is OffsetRange && range.end == null) {
                    range = range.copy(end = range.start + annotation.defaultSize - 1)
                }
            }
            if (annotation.maxSize > 0) {
                if (range is OffsetRange && (range.end == null || range.end!! - range.start + 1 > annotation.maxSize)) {
                    throw IllegalArgumentException("Range size exceeded, maximum is ${annotation.maxSize}")
                }
            }
        }
        return range
    }

    private fun parseRange(req: NativeWebRequest): Range? {
        val header = req.getHeader("Range")
        val res = if (header != null) {
            headerFormat.matchEntire(header) ?: throw IllegalArgumentException("Invalid Range header")
        } else {
            val param = req.getParameter("_range") ?: return null
            paramFormat.matchEntire(param) ?: throw IllegalArgumentException("Invalid _range parameter")
        }
        val b1 = res.groupValues[1].toIntOrNull()
        val b2 = res.groupValues[2].toIntOrNull()
        return if (b1 != null) {
            if (b2 != null && b2 < b1) throw IllegalArgumentException("Invalid Range header")
            OffsetRange(b1, b2)
        } else {
            if (b2 == null) throw IllegalArgumentException("Invalid Range header")
            EndOffsetRange(b2)
        }
    }

}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequestRange(val defaultSize: Int = 0, val maxSize: Int = 0)