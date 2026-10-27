import ExpoModulesCore
import UniformTypeIdentifiers

/**
 * iOS counterpart. Files opened "with" an app arrive as file:// URLs that
 * already end with their real name, so this reads it from the URL, with the
 * size and the MIME type the extension implies. Never throws.
 */
public class ExpoFileDisplayNameModule: Module {
  public func definition() -> ModuleDefinition {
    Name("ExpoFileDisplayName")

    Function("getDisplayName") { (uri: String) -> String? in
      return Self.info(uri)["name"] as? String
    }

    Function("getInfo") { (uri: String) -> [String: Any?] in
      return Self.info(uri)
    }
  }

  private static func info(_ uri: String) -> [String: Any?] {
    guard let url = URL(string: uri), url.isFileURL else {
      return ["name": nil, "size": nil, "mimeType": nil]
    }
    let name = url.lastPathComponent.isEmpty ? nil : url.lastPathComponent
    let size = (try? url.resourceValues(forKeys: [.fileSizeKey]))?.fileSize
    let mimeType = UTType(filenameExtension: url.pathExtension)?.preferredMIMEType
    return ["name": name, "size": size, "mimeType": mimeType]
  }
}
