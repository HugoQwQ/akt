#version 450

layout(location = 0) in vec2 in_position;
layout(location = 1) in vec2 in_uv;
layout(location = 2) in vec4 in_color;

layout(location = 0) out vec2 out_uv;
layout(location = 1) out vec4 out_color;

layout(push_constant) uniform push_constants {
    vec2 screen_size;
} push;

void main() {
    vec2 normalized = in_position / push.screen_size;
    gl_Position = vec4(
        normalized.x * 2.0 - 1.0,
        normalized.y * 2.0 - 1.0,
        0.0,
        1.0
    );
    out_uv = in_uv;
    out_color = in_color;
}
