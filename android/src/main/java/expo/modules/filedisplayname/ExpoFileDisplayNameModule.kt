package expo.modules.filedisplayname

import android.net.Uri
import android.provider.OpenableColumns
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

/**
 * The real name of a file handed over by URI. An Android content:// URI
 * (MediaStore, WhatsApp, Gmail, Downloads...) usually carries no name at all:
 * the name is a column the provider answers (OpenableColumns.DISPLAY_NAME),
 * which apps must query. This module asks it, plus SIZE and the MIME type.
 * Never throws: a provider that refuses, or an unknown scheme, gives nulls.
 */
class ExpoFileDisplayNameModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoFileDisplayName")

    Function("getDisplayName") { uri: String ->
      info(uri)["name"]
    }

    Function("getInfo") { uri: String ->
      info(uri)
    }
  }

  private fun info(uriString: String): Map<String, Any?> {
    val uri = try {
      Uri.parse(uriString)
    } catch (e: Exception) {
      return empty()
    }
    return when (uri.scheme) {
      "file" -> {
        val file = uri.path?.let { java.io.File(it) }
        mapOf(
          "name" to uri.lastPathSegment,
          "size" to file?.takeIf { it.exists() }?.length(),
          "mimeType" to null
        )
      }
      "content" -> queryContent(uri)
      else -> empty()
    }
  }

  private fun queryContent(uri: Uri): Map<String, Any?> {
    val resolver = appContext.reactContext?.contentResolver ?: return empty()
    var name: String? = null
    var size: Long? = null
    try {
      resolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null
      )?.use { cursor ->
        if (cursor.moveToFirst()) {
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex >= 0 && !cursor.isNull(nameIndex)) name = cursor.getString(nameIndex)
          val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
          if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
        }
      }
    } catch (e: Exception) {
      // SecurityException (no grant), provider crash...: fall through to nulls.
    }
    val mimeType = try {
      resolver.getType(uri)
    } catch (e: Exception) {
      null
    }
    return mapOf("name" to name, "size" to size, "mimeType" to mimeType)
  }

  private fun empty(): Map<String, Any?> = mapOf("name" to null, "size" to null, "mimeType" to null)
}
