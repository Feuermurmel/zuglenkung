package streckenplan.interfaces;

import streckenplan.sched.Steppable;
import view.painter.Paintable;

public interface Simulation extends Steppable {
	Layout getLayout();
	Paintable getPaintable();
}
