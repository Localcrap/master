import java.util.ArrayList;

public class Sphere implements RObject{
	//pos and shape of circle
	double radius;
	Vec3 center;
	double radius2;
	public Surf surf;
	
	
	//	
	//radius,center,colour,kdiff,kspec,ktran,refrindex
	public Sphere(double r,Vec3 center,Vec3 colour,double kdiff,double kspec,double ktran,double refrindex,double ktlucence,Vec3 emission) {
		this.radius = r; 
		this.center = center;
		this.radius2= r*r;
		//TODO:add to Surf;
		surf = new Surf();
		surf.colour = colour;
		surf.kdiff = kdiff;
		surf.kspec = kspec;
		surf.ktran = ktran;
		surf.refrindex = refrindex;
		surf.ktlucence = ktlucence;
		surf.emission_colour = emission;
		
		
	}
	@Override
	
	public int intersection(Ray ray,Isect[] hit ) {
		int nroots;
		double b,disc,t1,t2;
		Vec3 v;
		int hitpos = 0;
		v = center.sub(ray.origin); // l
		b = v.dot(ray.direction); //tca
		disc = b*b-v.dot(v)+radius2;
		if(disc<= 0) {
			return 0;
		}
		disc = Math.sqrt(disc);
		t2 = b+disc;
		if(t2<=raytracer.rayeps) {
			return 0;
		}
		t1 = b-disc;
		if(t1>raytracer.rayeps) {
			//entering sphere
			hit[hitpos] = (new Isect(t1,this,1,null));
			hitpos++;
			nroots = 2;
		}
		else {
			nroots = 1;
		}
		//exiting sphere
		hit[hitpos] = (new Isect(t2,this,0,this.surf));
		
		
		
		return nroots;
	}
	public double testintersection(Ray r,double distance,Isect[] hit) {
		
		Vec3 l = center.sub(r.origin);
		double tca = l.dot(r.direction);
		if(tca< 0 ) {
			return 0;
		}
		double d2 = l.dot(l)-tca*tca;
		if(d2 > radius2) {
			return 0;
		}
		double thc = Math.sqrt(radius2-d2);
		double t0 = tca -thc; 
		hit[0] = (new Isect(t0,this,1,null));
		double t1 = tca+thc; 
		hit[1] = (new Isect(t1,this,0,this.surf));
		
		return 2;
	}

	
	
	@Override
	public Vec3 normal(Vec3 p) {
		//Vec3 p,n;
		Vec3 n;
		n =  p.sub(center);
		n.normalize();
		return n;
	}
	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public raytracer.test name() {
		return raytracer.test.SPHERE;
	}
	@Override
	public Surf getSurf() {
		return surf;
	}
	
    
}
