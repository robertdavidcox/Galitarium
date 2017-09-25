attribute vec4 aPosition;
attribute vec4 aColor;
attribute float aSize;
varying vec4 vColor;
uniform mat4 u_MVPMatrix;
uniform float zoom_factor;

void main() {
  gl_PointSize = aSize / zoom_factor;
  vColor = aColor;
  gl_Position = u_MVPMatrix * aPosition;
}                                                 