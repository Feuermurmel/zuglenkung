package util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComboBox;

public class SwingUtil {
	private SwingUtil() {
	}

	public static <V> JComboBox comboBox(final List<V> values, ItemViewStrategy<V> strategy, final SingleItemListener<V> listener) {
		final JComboBox comboBox = new JComboBox();

		for (V i : values)
			comboBox.addItem(strategy.asString(i));
		
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.changed(values.get(comboBox.getSelectedIndex()));
			}
		});
		
		return comboBox;
	}

	public interface SingleItemListener<V> {
		void changed(V value);
	}

	public interface ItemViewStrategy<V> {
		String asString(V value);
	}
}
