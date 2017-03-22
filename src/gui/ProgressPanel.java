package gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressPanel extends JPanel {

	/**
     * 
     */
    private static final long serialVersionUID = 3084149753754099928L;
	private static final int MAX_PROGRESS = 1000;

	public ProgressPanel(ConversionSettings converter) {
	    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	    add(new JProgressBar(0, MAX_PROGRESS));
	    add(new JLabel("."));
    }

}
