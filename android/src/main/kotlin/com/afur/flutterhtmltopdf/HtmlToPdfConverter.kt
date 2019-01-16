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

    /** callback interface to get the result back after created pdf file */
    interface Callback {
        fun success(filePath: String)
        fun failure()
    }

    companion object {

        private val REQUEST_CODE = 101

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

            val outputPath = activity.applicationContext.applicationInfo.dataDir
            val path = Environment.getExternalStoragePublicDirectory("$outputPath/pdf/")
            val fileName = "GeneratedFromHtml.pdf"

            //check the marshmallow permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
                    callback.failure()
                    return
                }
            }

            val jobName = "Generate PDF"
            var attributes: PrintAttributes? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                attributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()
            }
            val pdfPrint = PdfPrinter(attributes!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pdfPrint.print(webView.createPrintDocumentAdapter(jobName), path, fileName, object : PdfPrinter.CallbackPrint {
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