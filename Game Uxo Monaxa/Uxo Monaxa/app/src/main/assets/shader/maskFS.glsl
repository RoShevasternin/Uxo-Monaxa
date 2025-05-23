#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;    // Основна текстура
uniform sampler2D u_mask;       // Текстура маски

void main() {
    vec4 maskColor = texture2D(u_mask, v_texCoords);    // Отримуємо колір з маски
    vec4 texColor  = texture2D(u_texture, v_texCoords); // Отримуємо колір з основної текстури

    // Використовуємо альфа-канал маски для визначення прозорості текстури
    texColor.a *= maskColor.a;

    gl_FragColor = v_color * texColor;  // Повертаємо фінальний колір з урахуванням альфа-каналу групи
}
