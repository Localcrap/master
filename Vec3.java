public class Vec3 {
	
	public double x,y,z;
	
	public Vec3(double x,double y,double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Vec3(double x) {
		this.x=x;
		this.y=x;
		this.z=x;
	}
	
	
	public double length() {
		return Math.sqrt(length2());
	}
	public double length2() {
		return x*x+y*y+z*z;
	}
	public void normalize() {
		double nor2 = length2();
		if (nor2 > 0) {
			double invNor = 1/ Math.sqrt(nor2);
			x *= invNor;
			y *= invNor;
			z *= invNor;
			
		}
		else {
			assert(false);
		}
	}
	public double dot(Vec3 v) {
		return  x*v.x+y*v.y+z*v.z;

	}
	public Vec3 add(Vec3 v) {
		return new Vec3(x+v.x,y+v.y,z+v.z);
		
	}
	public Vec3 sub(Vec3 v) {
		return new Vec3(x-v.x,y-v.y,z-v.z);
		
	}
	public  Vec3 addS(double t,Vec3 b) {
		return new Vec3(x*t+b.x,y*t+b.y,z*t+b.z);
	}
	public  Vec3 negate() {
		return new Vec3(x*-1,y*-1,z*-1);
	}
	public Vec3 copy() {
		return new Vec3(x,y,z);
	}
	public double distance(Vec3 p) {
		
		return (x-p.x)*(x-p.x) +(y-p.y)*(y-p.y)+(z-p.z)*(z-p.z) ;
	}
	public Vec3 mult(double t) {
		return new Vec3(x*t,y*t,z*t);
		
	}
	
	public Vec3 mult(Vec3 t) {
		return new Vec3(x*t.x,y*t.y,z*t.z);
		
	}
	public void setZero() {
		x = 0;
		y = 0; 
		z = 0;
	}
	public Vec3 comb(double a,Vec3 B,double b ) {
		return new Vec3(a*x+b*B.x,a*y+b*B.y,a*z+b*B.z);
	}
	public void setValues(int x,int y,int z) {
		this.x= x; 
		this.y=y;
		this.z= z;
	}
	public void setValuesV(Vec3 v) {
		this.x= v.x; 
		this.y=v.y;
		this.z= v.z;
	}
	public Vec3 cross(Vec3 v){
		return new Vec3((y*v.z)-(z*v.y),(z*v.x)-(x*v.z),(x*v.y)-(y*v.x));
	}
	
	
}