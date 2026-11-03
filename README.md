# akt

a visual novel game framework written in kotlin.

## build

```powershell
.\gradlew.bat build
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
