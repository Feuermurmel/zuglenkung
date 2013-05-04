package streckenplan.interfaces;

import view.Steppable;
import view.painter.Paintable;

public interface Simulation extends Steppable {
	Layout getLayout();
	Paintable getPaintable();
}
