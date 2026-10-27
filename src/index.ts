import { requireOptionalNativeModule } from 'expo';

/** What the platform says about a file URI. Any field can be null. */
export interface FileUriInfo {
  /** The real file name, extension included (Android: OpenableColumns.DISPLAY_NAME). */
  name: string | null;
  /** Size in bytes, when the provider tells it. */
  size: number | null;
  /** MIME type (Android: ContentResolver.getType; iOS: from the extension). */
  mimeType: string | null;
}

interface NativeModule {
  getDisplayName(uri: string): string | null;
  getInfo(uri: string): FileUriInfo;
}

// Optional: an app binary built before this module was added simply gets nulls.
const native = requireOptionalNativeModule<NativeModule>('ExpoFileDisplayName');

/** True when the native side is linked in this build. */
export const isAvailable = native != null;

/**
 * The real name of the file behind `uri` (`content://` or `file://`), or null.
 * Synchronous; never throws.
 */
export function getDisplayName(uri: string): string | null {
  try {
    return native?.getDisplayName(uri) ?? null;
  } catch {
    return null;
  }
}

/** Name, size and MIME type of the file behind `uri`; nulls when unknown. */
export function getInfo(uri: string): FileUriInfo {
  try {
    return native?.getInfo(uri) ?? { name: null, size: null, mimeType: null };
  } catch {
    return { name: null, size: null, mimeType: null };
  }
}
