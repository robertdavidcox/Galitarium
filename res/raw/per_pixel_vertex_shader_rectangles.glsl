attribute vec4 aPosition;
attribute vec4 aColor;
attribute float aSize;
varying vec4 vColor;
uniform mat4 u_MVPMatrix;

void main() {
  gl_PointSize = aSize;
  vColor = aColor;
  gl_Position = u_MVPMatrix * aPosition;
}                                                 