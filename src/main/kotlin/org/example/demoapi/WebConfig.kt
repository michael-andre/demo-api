package org.example.demoapi

import org.example.demoapi.api.RangeResolver
import org.example.demoapi.api.YamlHttpMessageConverter
import org.example.demoapi.utils.SortSpec
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.format.FormatterRegistry
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.lang.reflect.ParameterizedType

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(RangeResolver)
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(YamlHttpMessageConverter)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
                .addMapping("/**")
                .allowedMethods("GET", "PATCH", "POST", "DELETE", "PUT")
                .allowedHeaders("content-type", "range")
                .exposedHeaders("content-range")
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(object : ConverterFactory<String, Enum<*>> {

            override fun <T : Enum<*>?> getConverter(enumClass: Class<T>): Converter<String, T> {
                return Converter { value ->
                    enumClass.enumConstants.firstOrNull { it?.name.equals(value, ignoreCase = true) }
                }
            }

        })
        registry.addConverter(object : GenericConverter {

            override fun getConvertibleTypes() =
                    setOf(GenericConverter.ConvertiblePair(String::class.java, SortSpec::class.java))

            override fun convert(value: Any?, stringDescr: TypeDescriptor, sortSpecDescr: TypeDescriptor): SortSpec<*> {
                val enumClass = sortSpecDescr.resolvableType.generics.first().rawClass as Class<out Enum<*>>
                val rawKey = value.toString().split('.')
                val key = enumClass.enumConstants.first { it.name.equals(rawKey[0], ignoreCase = true) }
                return SortSpec(
                        key = key,
                        direction = when (rawKey[1]) {
                            "desc" -> SortSpec.Direction.DESC
                            else -> SortSpec.Direction.ASC
                        }
                )
            }
        })
    }

}