package gui;

import gui.converter.Converter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This is the main conversion step.
 * 
 * @author michael
 */
public class ConversionStep implements ContentStep {
	private JPanel content;

	private boolean started = false;

	private final ConversionSettings data;

	private Converter converter;

	private JButton startButton;

	private JButton abortButton;

	private JPanel inner;

	private JTextArea status;

	public ConversionStep(ConversionSettings data) {
		this.data = data;
	}

	@Override
	public String getTitle() {
		return "Konvertieren";
	}

	@Override
	public JComponent getContent() {
		if (content == null) {
			content = generateContent();
			setConversionStatus(0, "", "");
		}

		return content;
	}

	private synchronized JPanel generateContent() {
		JPanel south = new JPanel();

		startButton = new JButton("Konvertieren");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startConversion();
			}
		});
		south.add(startButton);

		abortButton = new JButton("Abbrechen");
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abortConversion();
			}
		});
		south.add(abortButton);

		inner = new JPanel();
		status = new JTextArea();
		inner.add(status);

		JPanel content = new JPanel(new BorderLayout());
		content.add(inner);
		content.add(south, BorderLayout.SOUTH);

		startButton.setEnabled(!started);
		abortButton.setEnabled(started);
		return content;
	}

	private synchronized void startConversion() {
		if (started) {
			return;
		}
		setStarted(true);

		setConversionStatus(0, "Konvertiere", "Wird gestartet");
		converter = new Converter(data);
		Thread converterThread = new Thread(new Runnable() {
			@Override
			public void run() {
				doConversion();
			}

		});
		converterThread.start();
	}

	private void setStarted(boolean b) {
	    started = b;
		startButton.setEnabled(!started);
		abortButton.setEnabled(started);
    }

	/**
	 * Runs in converter thread.
	 */
	private void doConversion() {
		try {
			converter.startConversion();
			setConversionStatus(1, "Fertig", "Alles Konvertiert");
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			setConversionStatus(1, "Fehler", stringWriter.toString());
		}
	}

	private synchronized void abortConversion() {
		if (!started) {
			return;
		}

		setStarted(false);
		setConversionStatus(0, "Abgebrochen", "Durch Benutzer abgebrochen.\n"
		        + "Warnung: Das Ergebnis ist undefiniert!");

		converter.stop();
	}

	private void setConversionStatus(float progress, String title,
	        String description) {
		inner.setBorder(BorderFactory.createTitledBorder(title));
		status.setText(description);
	}

}
