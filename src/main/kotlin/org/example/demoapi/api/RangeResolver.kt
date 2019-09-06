package org.example.demoapi.api

import org.example.demoapi.utils.Range
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

object RangeResolver : HandlerMethodArgumentResolver {

    private val headerFormat = Regex("items=(\\d+)?-?(\\d+)?")

    override fun supportsParameter(parameter: MethodParameter) =
            parameter.parameterType.isAssignableFrom(Range::class.java)

    override fun resolveArgument(
            parameter: MethodParameter,
            mvc: ModelAndViewContainer?,
            req: NativeWebRequest,
            binderFactory: WebDataBinderFactory?
    ): Range? {
        val header = req.getHeader("Range") ?: return null
        val results = headerFormat.matchEntire(header) ?: throw IllegalArgumentException("Invalid Range header")
        return Range(
                results.groupValues[1].toIntOrNull(),
                results.groupValues[2].toIntOrNull()
        )
    }

}