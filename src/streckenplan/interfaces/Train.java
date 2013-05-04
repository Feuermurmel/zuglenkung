package streckenplan.interfaces;

import java.util.List;

public interface Train {
	double getLength();
	List<Car> getCars();
	double getCurrentSpeed();
	void setTargetSpeed(double speed);
	Car addCar(double length);
}
