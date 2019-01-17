package com.afur.flutterhtmltopdf

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.print.PdfPrinter
import android.print.PrintAttributes
import android.webkit.WebView
import android.webkit.WebViewClient

import java.io.File


class HtmlToPdfConverter {

    interface Callback {
        fun success(filePath: String)
        fun failure()
    }

    companion object {
        fun convert(filePath : String, activity: Activity, callback: Callback) {
            val webView = WebView(activity.applicationContext)
            val htmlContent = File(filePath).readText(Charsets.UTF_8)
            webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    createPdfFromWebView(webView, activity, callback)
                }
            }
        }

        fun createPdfFromWebView(webView: WebView, activity: Activity, callback: Callback) {
            val path = activity.applicationContext.filesDir
            val fileName = "GeneratedFromHtmlContent.pdf"
            var attributes: PrintAttributes? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                attributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()
            }
            val pdfPrint = PdfPrinter(attributes!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pdfPrint.print(webView.createPrintDocumentAdapter("Generate PDF Document"), path, fileName, object : PdfPrinter.Callback {
                    override fun success(filePath: String) {
                        callback.success(filePath)
                    }

                    override fun onFailure() {
                        callback.failure()
                    }
                })
            }
        }
    }
}