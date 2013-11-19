package robot;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;

public class VariancePoseProvider extends OdometryPoseProvider {

	private float varX = 0, varY = 0, varH = 0;
	private float travelVar = 0.25f, rotateVar = 4;

	public VariancePoseProvider(MoveProvider mp) {
		super(mp);
	}

	public VariancePoseProvider(MoveProvider mp, float travelVar, float rotateVar) {
		super(mp);
		this.travelVar = travelVar;
		this.rotateVar = rotateVar;
	}

	@Override
	public synchronized VariancePose getPose() {
		Pose pose = super.getPose();
		return new VariancePose(pose.getX(), pose.getY(), pose.getHeading(), varX, varY, varH);
	}

	/**
	 * Sets the pose and variances
	 * 
	 * @param pose
	 */
	public synchronized void setPose(VariancePose pose) {
		super.setPose(pose);
		varX = pose.getVarX();
		varY = pose.getVarY();
		varH = pose.getVarH();
	}

	@Override
	public void moveStopped(Move move, MoveProvider mp) {
		super.moveStopped(move, mp);
		updateVariance(move);
	}

	private synchronized void updateVariance(Move event) {
		double dvarX = 0, dvarY = 0, dvarH = 0;
		float distance = event.getDistanceTraveled();
		float heading = getPose().getHeading();
		double headingRad = Math.toRadians(heading);
		if (event.getMoveType() == Move.MoveType.ROTATE) {
			dvarH = varToRadians(rotateVar);
		} else if (event.getMoveType() == Move.MoveType.TRAVEL) {
			double xterm1 = Math.pow(distance, 2) * Math.pow(Math.sin(headingRad), 2)
					* varToRadians(varH);
			double xterm2 = Math.pow(Math.cos(headingRad), 2) * travelVar;
			double xterm3 = travelVar * Math.pow(Math.sin(headingRad), 2) * varToRadians(varH);
			dvarX = xterm1 + xterm2 + xterm3;

			double yterm1 = Math.pow(distance, 2) * Math.pow(Math.cos(headingRad), 2)
					* varToRadians(varH);
			double yterm2 = Math.pow(Math.sin(headingRad), 2) * travelVar;
			double yterm3 = travelVar * Math.pow(Math.cos(headingRad), 2) * varToRadians(varH);
			dvarY = yterm1 + yterm2 + yterm3;
		}
		varX += dvarX;
		varY += dvarY;
		varH += varToDegrees(dvarH);
	}

	public void setTravelVar(float var) {
		travelVar = var;
	}

	public void setRotateVar(float var) {
		rotateVar = var;
	}

	private double varToRadians(double var) {
		return Math.pow(Math.PI / 180, 2) * var;
	}

	private double varToDegrees(double var) {
		return Math.pow(180f / Math.PI, 2) * var;
	}
}