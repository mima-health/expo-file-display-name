# expo-file-display-name

The **real file name** of a file URI in an Expo / React Native app, on Android and iOS.

Android hands files to apps as `content://` URIs that usually carry no name at all
(`content://media/external/audio/media/1000147322`, WhatsApp, Gmail, Downloads...).
The name exists, but as a column the provider answers (`OpenableColumns.DISPLAY_NAME`),
which JavaScript cannot query: `expo-file-system`'s `File.name` only cuts the URI at its
last `/`. This module asks the provider.

```ts
import { getDisplayName, getInfo } from 'expo-file-display-name';

getDisplayName('content://media/external/audio/media/1000147322'); // "Morning breath.m4a"
getInfo(uri); // { name: "Morning breath.m4a", size: 2672899, mimeType: "audio/mp4" }
```

- Synchronous, never throws: unknown scheme, refused provider or missing grant give `null`s.
- `file://` URIs work on both platforms (name, size, MIME type from the extension on iOS).
- The native side is optional at runtime: a binary built without it returns `null`s
  (`isAvailable` tells which), so JS can ship ahead of a rebuild.

Requires a development build or a release build (native code): not Expo Go.

## Install

Not on npm yet: install it from GitHub, pinned to a tag.

```sh
pnpm add github:mima-health/expo-file-display-name#v0.1.0
# or: npm install github:mima-health/expo-file-display-name#v0.1.0
# or: yarn add expo-file-display-name@github:mima-health/expo-file-display-name#v0.1.0
```

Then rebuild the native app (`npx expo run:android`, `npx expo run:ios`, or an EAS build):
Expo autolinking picks the module up from `node_modules`. The package ships its TypeScript
source; Metro compiles it like your own code.

## Why

Built for Ikima, a local-first meditation journal: a file opened from a file manager came
in titled "1000147322".

## License

MIT, see [LICENSE](LICENSE).
