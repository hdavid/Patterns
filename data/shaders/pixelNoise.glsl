uniform mat4 projmodelviewMatrix;

attribute vec4 inVertex;
attribute vec4 inColor;

varying vec4 vertColor;

void main() {
	gl_Position = projmodelviewMatrix * inVertex;
	
	vec2 vec = vec2(inVertex.x,inVertex.y);
	float rand = random(vec);
	vertColor = inColor * 0;
	
}


float random( vec2 p )
{
  // We need irrationals for pseudo randomness.
  // Most (all?) known transcendental numbers will (generally) work.
  const vec2 r = vec2(
    23.1406926327792690,  // e^pi (Gelfond's constant)
     2.6651441426902251); // 2^sqrt(2) (Gelfond–Schneider constant)
  return fract( cos( mod( 123456789., 1e-7 + 256. * dot(p,r) ) ) );  
}