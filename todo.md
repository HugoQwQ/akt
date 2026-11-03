# first release todo

## completed foundation

- [x] create the `akt-core`, `akt-assets`, `akt-ui`, `akt-render`,
  `akt-runtime`, and `example` modules.
- [x] add shared jvm, library, application, and desktop conventions in
  `buildSrc`.
- [x] implement the kotlin story dsl, branching sessions, characters, choices,
  actions, transforms, transitions, and text reveal.
- [x] implement compressed and encrypted asset archives with directory and
  archive stores.
- [x] implement the renderer backend and factory boundaries.
- [x] implement the lwjgl `3.4.3` vulkan backend with swapchain recreation,
  texture uploads, drawing batches, input, and framebuffer capture.
- [x] render dialogue, speaker names, choices, images, placeholders, and text.
- [x] add story, archive, and shader resource tests.
- [x] verify build, startup, resize, capture, and graceful cleanup.
- [x] add ktfmt checks for source, scripts, and `buildSrc`.
- [x] add github actions formatting checks on every push.
- [x] add tagged release builds for version and alpha tags.
- [x] add an append-only maven repository on github pages.

## release blockers

### public api and documentation

- [x] freeze the `0.1.0` public api and remove the `-snapshot` version.
- [x] add kdoc to every public type, function, property, and error condition.
- [x] document supported jvm versions and vulkan requirements.
- [x] document the module dependency graph and renderer factory contract.
- [x] add a migration policy for changes after `0.1.0`.
- [x] add a complete authoring guide with renderer-independent usage.
- [x] publish api documentation from the release build.

### story and runtime

- [ ] execute declared enter-scene and dialogue actions.
- [ ] implement set variable, emit event, wait, and play sound handlers.
- [ ] define stable save state containing scene, line, variables, and choices.
- [ ] add save, load, and restart apis.
- [ ] add tests for every transition and story completion path.
- [ ] prevent empty-scene cycles from recursing without a bound.
- [ ] define behavior for invalid missing assets in development and release
  builds.

### text and gui

- [ ] replace the ascii atlas with a configurable unicode font provider.
- [ ] add kerning, fallback glyphs, and per-language line breaking.
- [ ] add text overflow tests for narrow and resized viewports.
- [ ] support mouse selection for choices in addition to number keys.
- [ ] add focus and keyboard navigation for all gui controls.
- [ ] add a reduced-motion setting that resolves transitions immediately.
- [ ] add a gui skin or theme configuration.
- [ ] add visual regression fixtures for dialogue, choices, and transitions.

### renderer

- [ ] test startup on at least two vulkan vendors and on moltenvk.
- [ ] handle surface loss by recreating the surface and swapchain.
- [ ] handle device loss with a clear fatal error and restart guidance.
- [ ] support minimized windows without spinning or acquiring images.
- [ ] add graphics pipeline caching.
- [ ] move and reuse vertex buffers instead of uploading one frame buffer.
- [ ] add optional vulkan validation-layer configuration for development.
- [ ] add an opt-in software or headless backend for ci visual tests.
- [ ] verify alpha, srgb color, texture filtering, and rotation behavior.

### assets

- [ ] add a gradle task for packing development assets.
- [ ] add a standalone asset-pack command with key-file password input.
- [ ] add archive integrity checks and format compatibility tests.
- [ ] add archive key listing and unpacking for debugging.
- [ ] add size limits and clearer corrupt-entry errors.
- [ ] add image format validation before renderer upload.
- [ ] define asset naming conventions for backgrounds, portraits, audio, and
  fonts.
- [ ] document the limits of client-side encryption.

### packaging and release

- [x] configure maven publication for all library modules.
- [x] publish sources and javadoc artifacts.
- [x] make desktop natives reproducible per target platform.
- [ ] add a distributable example jar or native image.
- [ ] add signed windows release packaging instructions.
- [ ] verify distribution works from a path containing spaces.
- [ ] create the `0.1.0` changelog and release notes.
- [ ] add repository license, contribution, and security documents.
- [x] add a ci workflow for build and non-graphical tests.

## release candidate checklist

- [ ] `.\gradlew.bat build` passes from a clean checkout.
- [ ] `.\gradlew.bat :example:run` starts with a local vulkan loader.
- [ ] the example story can be completed by both keyboard and pointer.
- [ ] resize, minimize, restore, capture, and close are tested.
- [ ] encrypted and unencrypted archives both boot the example.
- [ ] no unexpected errors are logged during a five-minute run.
- [ ] release artifacts are tested on a machine without a jdk.
- [ ] user-facing documentation matches the release artifacts.
- [ ] all release artifacts use the final version and sha-256 checksums.

## deferred after 0.1.0

- audio mixing and music transitions.
- voice playback and lip-sync hooks.
- localization catalogs and runtime language switching.
- rich text markup, ruby text, and accessibility narration.
- latex support
- hot reload.
- saves and platform services.
