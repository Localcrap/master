import java.util.ArrayList;

public class RayAlg {


    public static void altTrace(int level,double weight ,Ray ray, Vec3 col) {
    	double tnear = Double.MAX_VALUE;
    	RObject sphere = null;
		TriangleMesh tm;
    	Isect[] hit = new Isect[2];
    	//TODO: fix repeated code
    	for(int i = 0; i<raytracer.objects.length;i++) {
			switch(raytracer.objects[i].name()){
				case SPHERE:
					if(raytracer.objects[i].intersection(ray, hit)>0) {
						//if(hit[0].t<0){hit[0] = hit[1];}
						if(hit[0].t < tnear) {
							tnear = hit[0].t;
							sphere = (Sphere)raytracer.objects[i];
						}
					}
					break;


				case TRIANGLEMESH:
					if(raytracer.objects[i].intersection(ray, hit)>0) {
						//if(hit[0].t<0){hit[0] = hit[1];}
						if(hit[0].t < tnear) {
							tnear = hit[0].t;
							sphere = (TriangleMesh)raytracer.objects[i];
						}
					}
					break;
			}
    		
    	}

    	if (sphere == null) {
    		shadeBackground(ray,col);
    		return;
    	}
    	shade(level,weight,ray,tnear,sphere,hit,col);
    	
    	
    	//test stuff
    	Vec3 I  = ray.direction;
    	
    	Surf surf;
    	Ray tray = new Ray();
		Vec3 P = raytracer.rayPoint(ray,hit[0].t);
		Vec3 N = sphere.normal(P);
		Vec3 tcol = new Vec3(0,0,0);
    	
    	if(level+1 < raytracer.maxlevel) {
    		tray.origin = P.copy();
    		
    		surf = hit[0].prim.getSurf();
    		/*
    		if(surf.kspec*weight > raytracer.minweight) {
    			tray.direction = specularDirection(I,N);
    			trace(level+1,surf.kspec*weight,tray,tcol);
    			col.setValuesV(tcol.addS(surf.kspec, col));
    			
    		}
    		
    		if(surf.ktran*weight> raytracer.minweight) {
    			if(TransmissionDirection(hit,I,N,tray)) {
    				altTrace(level+1,surf.ktran*weight,tray,tcol);
    				col.setValuesV( tcol.addS(surf.ktran, col));
    				
    			}
    		}
    		*/
    		
    	}
    	
    	
    	
    	
    	
    	
    }
    private static double mix(double a, double b, double mix) {
    	return b * mix + a* (1-mix);
    }
    
    private static void shadeBackground(Ray ray, Vec3 col) {
		col.setValues(2, 2,2 );
		
	}
	public static void shade(int level,double weight,Ray ray,double tnear,RObject sphere ,Isect[]  hit,Vec3 col ) {

		col.setZero();
    	Vec3 phit = ray.origin.add(ray.direction.mult(tnear));
    	Vec3 nhit = sphere.normal(phit);
    	nhit.normalize();
		Surf surf =  sphere.getSurf();
    	
    	
    	double bias =  0.0001;
    	boolean inside = false;
    	if (ray.direction.dot(nhit)>0) {
    		nhit = nhit.negate();
    		inside = true;
    		
    		
    	}
    	//if not defuse
    	if( ((surf.ktlucence> 0) || (surf.kspec> 0)) &&  level < raytracer.maxlevel) {
    		Vec3 reflection = new Vec3(0);
    		Vec3 refraction = new Vec3(0);
    			
    		
    		
    		double facingratio = -ray.direction.dot(nhit);
    		double fresneleffect = mix(Math.pow(1-facingratio, 3),1,0.1);
    		
    		//do reflection
    		if(surf.kspec> 0) {
        		Vec3  refldir  = ray.direction.sub(nhit.mult(2).mult(ray.direction.dot(nhit)));
        		
        		refldir.normalize();
        		 //colour;
        		altTrace(level+1,weight,new Ray(phit.add(nhit.mult(bias)),refldir),reflection);
    		}

    		
    		
    		//do refraction
    		if(surf.ktlucence>0) {
    			double ior = 1.1, eta = (inside) ? ior : 1 / ior; // are we inside or outside the surface? 
    			double cosi = -nhit.dot(ray.direction);
    			double  k = 1 - eta * eta * (1 - cosi * cosi); 
    			Vec3 refrdir = ray.direction.mult(eta).add(nhit.mult(eta*cosi-Math.sqrt(k)));
    			refrdir.normalize();
    			altTrace(level+1,weight,new Ray(phit.sub(nhit.mult(bias)),refrdir),refraction);
    			
    			
    		}
    		Vec3 temp1, temp2;
    		temp2 = refraction.mult((1-fresneleffect)*surf.ktlucence);
    		temp1 = reflection.mult(fresneleffect);
    		temp1 = temp1.add(temp2);
    		temp1 = temp1.mult(surf.colour);
    		col.setValuesV(temp1);
    		
    		
    	}
    	else {
    		for(int i = 0;i< raytracer.objects.length;i++) {
    			if(raytracer.objects[i] != sphere && raytracer.objects[i].getSurf().emission_colour != null) {
    				Vec3 transmission = new Vec3(1);
    				Vec3 lightDirection = ((Sphere)raytracer.objects[i]).center.sub(phit);
    				lightDirection.normalize();
    				for(int j = 0;j<raytracer.objects.length;j++) {
    					if(i!=j) {
    						if(raytracer.objects[j].intersection(new Ray(phit.add(nhit.mult(bias)),lightDirection), hit)>0) {
    							transmission.setZero();;
    							break;
    						}
    					}
    				}
    				col.setValuesV(col.add(surf.colour.mult(transmission).mult
    						(Math.max(0.,nhit.dot(lightDirection))).mult(raytracer.objects[i].getSurf().emission_colour)      ));
    				
    			}
    		}
    	}
    	if(surf.emission_colour != null) {
    		col.setValuesV(col.add(surf.emission_colour));
    	}
		
		

    }
    //based on Heckbert-Hanranhan84
    private static boolean TransmissionDirection(Isect[] hit, Vec3 I, Vec3 N, Ray tray) {
		double n1, n2, eta, c1, cs2;
		n1 = hit[0] != null && hit[0].medium != null ? hit[0].medium.refrindex : 1.;
		n2 = hit[1] != null && hit[1].medium != null ? hit[1].medium.refrindex : 1.;
		eta = n1/n2;
		
		c1 = -I.dot(N);
		cs2 = 1.-eta*eta*(1.-c1*c1);
		if(cs2<0.) return false;
		tray.direction = I.comb(eta, N, eta*c1-Math.sqrt(cs2));
		
		
		return false;
	}
	private static Vec3 specularDirection(Vec3 N, Vec3 I) {
		return N.addS(-2.*I.dot(N), I);
	}
	public static double shadow(Ray ray,double tmax) {
		int nhit;
		Isect[] hit = new Isect[raytracer.ISECTMAX];
		int hitpos = 0;
		
		for( int i = 0; i<raytracer.objects.length;i++) {
			if(raytracer.objects[i].intersection(ray, hit)== 0) {
				return 1.;
			}
			if( hit[0].t > raytracer.rayeps) {
				return 1.;
			}

		}
    	return 0;
    }
}

