# akt repository guide

## purpose

akt is a reusable visual novel framework and library. keep game authoring
code independent from the desktop renderer unless a module explicitly owns
that integration.

## architecture rules

- put story definitions, state transitions, text behavior, and rendering
  contracts in `akt-core`.
- put archive packing, compression, encryption, and asset lookup in
  `akt-assets`.
- put framework-independent layout and ui scene descriptions in `akt-ui`.
- put lwjgl, glfw, and vulkan implementation details in `akt-render`.
- keep backend implementations behind the `renderer backend` contract.
- put game lifecycle and input orchestration in `akt-runtime`.
- put shared gradle conventions in `buildSrc`, including library, application,
  desktop, test, and api-policy settings.
- keep `example` focused on public authoring and extension apis.
- avoid cyclic dependencies between modules.
- keep platform-specific code behind renderer or asset abstractions.

## style

- write comments, documentation, user-facing strings, and log messages in
  lower case.
- use kotlin and keep public apis explicit and documented.
- use slf4j for logging. the runtime supplies logback with ansi colors.
- use lwjgl `3.4.3`.
- use vulkan through the system vulkan loader. ship checked-in spir-v shaders so
  players do not need a shader compiler.
- use the gradle wrapper.
- use ktfmt with kotlin language style, a 100-column limit, and unused import
  removal. run the formatting checks before handing off changes.

## verification

run these before handing off changes:

```powershell
.\gradlew.bat build
.\gradlew.bat ktfmtCheck ktfmtCheckScripts
.\gradlew.bat -p buildSrc ktfmtCheck ktfmtCheckScripts
.\gradlew.bat :example:run
```

the run command opens a desktop window and needs a graphical session.
