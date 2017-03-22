package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is the main Program.
 * 
 * @author michael
 */
public class Main {
	private ConversionSettings converter;

	List<ContentStep> steps = new ArrayList<ContentStep>();
	List<StepLink> steplinks = new ArrayList<StepLink>();

	private final JLabel title = new JLabel();
	private final JPanel contentHolder = new JPanel();

	private int currentStepIndex;

	public Main() {
		converter = new ConversionSettings();
		// steps.add(new SelectDataBoundsStep(converter));
		steps.add(new SelectTilesStep(converter));
		steps.add(new ConversionStep(converter));

		JPanel root = generateRoot();

		generateFrame(root);

		showStep(steps.get(0));
	}

	private JPanel generateRoot() {
		JPanel upper = generateUpper();

		JPanel progressPanel = new ProgressPanel(converter);

		JPanel buttons = new JPanel();
		JButton back = new JButton("Zurück");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showStep(-1);
			}
		});
		buttons.add(back);
		JButton forward = new JButton("Weiter");
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showStep(1);
			}
		});
		buttons.add(forward);

		JPanel root = new JPanel();
		root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		root.setLayout(new BorderLayout());
		root.add(upper, BorderLayout.CENTER);
		root.add(progressPanel, BorderLayout.SOUTH);
		root.add(buttons, BorderLayout.AFTER_LAST_LINE);
		return root;
	}

	private JPanel generateUpper() {
		JPanel right = new JPanel(new BorderLayout());
		title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		title.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		right.add(title, BorderLayout.NORTH);
		right.add(contentHolder);

		JPanel upper = new JPanel(new BorderLayout());
		JPanel sidePanel = generateSidepanel();
		upper.add(sidePanel, BorderLayout.WEST);
		upper.add(right);
		return upper;
	}

	private void generateFrame(JPanel root) {
		JFrame frame = new JFrame("Main window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(root);
		frame.pack();
		frame.setSize(800, 800);
		frame.setVisible(true);
		frame.setFocusable(true);
	}

	private void showStep(int relative) {
		int newIndex = currentStepIndex + relative;
		if (newIndex >= 0 && newIndex < steps.size()) {
			showStep(steps.get(newIndex));
		}
	}
	
	private void showStep(ContentStep step) {
		title.setText(step.getTitle());

		contentHolder.removeAll();
		contentHolder.add(step.getContent());
		contentHolder.invalidate();
		contentHolder.repaint();

		for (StepLink link : steplinks) {
			link.setActive(step == link.step);
		}
		currentStepIndex = steps.indexOf(step);
	}

	private JPanel generateSidepanel() {
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.WHITE);
		sidePanel.setForeground(Color.BLACK);
		sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		for (ContentStep step : steps) {
			StepLink stepLabel = new StepLink(step);
			sidePanel.add(stepLabel);
			steplinks.add(stepLabel);
		}
		return sidePanel;
	}

	private class StepLink extends JLabel {
		/**
         * 
         */
		private static final long serialVersionUID = 517089484667322029L;
		private final ContentStep step;

		private StepLink(ContentStep step) {
			super(step.getTitle());
			this.step = step;
		}

		private void setActive(boolean active) {
			this.setForeground(active ? Color.BLACK : Color.DARK_GRAY);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
