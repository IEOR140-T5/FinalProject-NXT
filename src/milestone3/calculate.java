		Point current = new Point(0,echoDistance);
		float difference = Math.abs(normalize(bearings[0] - bearings[1]));
		
		try {
			// calculate x using the quadratic formula
		    double a = 1;
			double b = - hallWidth / Math.tan(Math.toRadians(difference));
			double c = - echoDistance * (hallWidth - echoDistance);
		
			x = (-b + Math.sqrt(Math.pow(b,2) - 4*a*c)) / (2*a);
			
			
			// quadratic formula
			double xFloat = (float) (-b + Math.sqrt(Math.pow(b,2) - 4*a*c)) / (2*a);
			current.x = (float) xFloat;
			
		} catch (Exception e) {
			if(difference == 0) {
				System.out.println("Error: Bearings are same.");
			} else {
				// calculate x using the quadratic formula
			    double a = 1;
				double b = 0;
				double c = - echoDistance * (hallWidth - echoDistance);
				
				// quadratic formula
				double xFloat = (-b + Math.sqrt(Math.pow(b,2) - 4*a*c)) / (2*a);
				current.x = (float) xFloat;
			}
		}
		
		// change the direction of x based on the orientation of the robot
		// facing -x direction and a bearing is greater than 90 degrees
		if ((_pose.getHeading() > 90 || _pose.getHeading() <= -90) && (Math.abs(normalize(bearings[0])) > 90  || Math.abs(normalize(bearings[1])) > 90)) {
			current.x = -current.x;
		} 
		// facing the +x direction and bearings are within 90 degrees
		else if ((_pose.getHeading() <= 90 && _pose.getHeading() > -90) && (Math.abs(normalize(bearings[0])) <= 90  || Math.abs(normalize(bearings[1])) <= 90) ) {
			current.x = -current.x;
		}

		
		
		Pose fixPosition(float[] bearings, float echoDistance) {
			float Y = beaconY;
			float x = 0;
			float y1 = echoDistance;
			float y2 = Y - y1;
			float c = normalize(bearings[0]-bearings[1]);
			float tanC = (float)Math.tan(Math.toRadians(c));
			float factor = -((float)Math.tan(Math.toRadians(c))*y1*y2);
			float negativeRoot = (float) (Y - Math.sqrt(Math.pow(Y,2)-(4*tanC*factor)))/(2*tanC);
			float positiveRoot = (float) (Y + Math.sqrt(Math.pow(Y,2)-(4*tanC*factor)))/(2*tanC);

			float x;
			float y = beaconY - echoDistance;
			float tan =  (float)Math.tan(Math.toRadians(normalize(bearings[0]-bearings[1])));
			float temp = -((float)Math.tan(Math.toRadians(normalize(bearings[0]-bearings[1])))*echoDistance*y);
			float delta = (float)Math.sqrt(Math.pow(beaconY,2)-(4*tan*temp));
			float root1 = (float)(beaconY - delta)/(2*tan);
			float root2 = (float)(beaconY + delta)/(2*tan);
			
			if (Math.abs(normalize(bearings[0]-bearings[1])) > 90){
				x = root1;
			} else {
				x = root2;
			}
			
			Pose pose = new Pose(x, echoDistance, normalize(_pose.angleTo(beacon[0]) - bearings[0]));	
			
			if (Math.abs(c)>90){
				x = negativeRoot;
			} else {
				x = positiveRoot;
			}


			Pose pose = new Pose(x, y1, normalize(_pose.angleTo(beacon[0]) - bearings[0]));	
			System.out.println("FIX");
			return pose;