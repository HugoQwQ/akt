# akt

a visual novel game framework written in kotlin.

## build

```powershell
.\gradlew.bat build
```

check or apply kotlin formatting:

```powershell
.\gradlew.bat ktfmtCheck ktfmtCheckScripts
.\gradlew.bat -p buildSrc ktfmtCheck ktfmtCheckScripts
.\gradlew.bat ktfmtFormat
.\gradlew.bat -p buildSrc ktfmtFormat
```

generate the public api documentation:

```powershell
.\gradlew.bat apiDocs
```

run the local release verification build, including api documentation:

```powershell
.\gradlew.bat release
```

## run the example

```powershell
.\gradlew.bat :example:run
```

press enter or space
to reveal and advance dialogue, number keys to choose, and escape to exit.

the desktop runtime requires a vulkan-capable graphics driver and loader. the
renderer uses glfw as a vulkan-only window provider and loads checked-in
spir-v shaders.
