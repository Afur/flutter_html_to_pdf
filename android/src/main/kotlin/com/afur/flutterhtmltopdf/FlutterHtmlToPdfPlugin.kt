package com.afur.flutterhtmltopdf

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class FlutterHtmlToPdfPlugin(private val registrar: Registrar) : MethodCallHandler {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_html_to_pdf")
            channel.setMethodCallHandler(FlutterHtmlToPdfPlugin(registrar))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "convertHtmlToPdf") {
            convertHtmlToPdf(call, result)
        } else {
            result.notImplemented()
        }
    }

    private fun convertHtmlToPdf(call: MethodCall, result: Result) {
        val htmlFilePath = call.argument<String>("htmlFilePath")

        HtmlToPdfConverter().convert(htmlFilePath!!, registrar.activity(), object : HtmlToPdfConverter.Callback {
            override fun onSuccess(filePath: String) {
                result.success(filePath)
            }

            override fun onFailure() {
                result.error("ERROR", "Unable to convert html to pdf document!", "")
            }
        })
    }
}

