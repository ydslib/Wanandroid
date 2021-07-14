package com.yds.base.net.convert

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NullConverterFactory(): Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val delegate: Converter<ResponseBody, *> = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter<ResponseBody, Any> { value ->
            if(value==null||value.contentLength()==0L){
                return@Converter null
            }
            delegate.convert(value)
        }
    }

    companion object{
        fun create() = NullConverterFactory()
    }
}