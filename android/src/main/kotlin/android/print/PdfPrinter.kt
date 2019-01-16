package android.print

import android.os.Build
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.util.Log

import java.io.File

class PdfPrinter(private val printAttributes: PrintAttributes) {

    fun print(printAdapter: PrintDocumentAdapter, path: File, fileName: String, callback: PdfPrinter.CallbackPrint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printAdapter.onLayout(null, printAttributes, null, object : PrintDocumentAdapter.LayoutResultCallback() {
                override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        printAdapter.onWrite(arrayOf(PageRange.ALL_PAGES), getOutputFile(path, fileName), CancellationSignal(), object : PrintDocumentAdapter.WriteResultCallback() {
                            override fun onWriteFinished(pages: Array<PageRange>) {
                                super.onWriteFinished(pages)

                                if (pages.size > 0) {
                                    val file = File(path, fileName)
                                    val path = file.absolutePath
                                    callback.success(path)
                                } else {
                                    callback.onFailure()
                                }
                            }
                        })
                    }
                }
            }, null)
        }
    }


    private fun getOutputFile(path: File, fileName: String): ParcelFileDescriptor? {
        if (!path.exists()) {
            path.mkdirs()
        }
        val file = File(path, fileName)
        try {
            file.createNewFile()
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e)
        }

        return null
    }


    interface CallbackPrint {
        fun success(path: String)
        fun onFailure()
    }

    companion object {
        private val TAG = PdfPrinter::class.java.simpleName
    }
}
