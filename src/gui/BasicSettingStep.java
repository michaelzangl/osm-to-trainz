package gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class BasicSettingStep implements ContentStep {
	
	public BasicSettingStep(ConversionSettings conversionData) {
		
	}

	@Override
    public String getTitle() {
	    return "Grundeinstellungen";
    }

	@Override
    public JComponent getContent() {
	    return new JLabel("TODO");
    }
}
