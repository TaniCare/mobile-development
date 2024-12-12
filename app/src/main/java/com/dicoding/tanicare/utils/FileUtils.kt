import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore

object FileUtils {

    fun getPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":")
                    if (split.size > 1 && "primary".equals(split[0], ignoreCase = true)) {
                        return "${context.getExternalFilesDir(null)}/${split[1]}"
                    }
                }

                isDownloadsDocument(uri) -> {
                    val id = DocumentsContract.getDocumentId(uri).toLongOrNull() ?: return null
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id
                    )
                    return getDataColumn(context, contentUri, null, null)
                }

                isMediaDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":")
                    if (split.size > 1) {
                        val contentUri = when (split[0]) {
                            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            else -> return null
                        }
                        return getDataColumn(context, contentUri, "_id=?", arrayOf(split[1]))
                    }
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        uri ?: return null
        var cursor: Cursor? = null
        val column = "_data"
        try {
            cursor =
                context.contentResolver.query(uri, arrayOf(column), selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri) =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri) =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority
}
