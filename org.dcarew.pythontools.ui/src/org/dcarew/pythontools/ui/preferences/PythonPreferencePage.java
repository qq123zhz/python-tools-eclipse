package org.dcarew.pythontools.ui.preferences;

import org.dcarew.pythontools.core.PythonCorePlugin;
import org.dcarew.pythontools.core.pylint.IPylintConfig;
import org.dcarew.pythontools.core.pylint.PylintConfigManager;
import org.dcarew.pythontools.core.utils.PythonLocator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Version;

public class PythonPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  private static String getVersionText() {
    Version version = PythonCorePlugin.getPlugin().getBundle().getVersion();

    return version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
  }

  private Text pythonText;
  private Text pylintText;
  private Combo combo;

  public PythonPreferencePage() {
    setDescription("Python Tools for Eclipse v" + getVersionText());
  }

  @Override
  public void init(IWorkbench workbench) {

  }

  @Override
  public boolean performOk() {
    PythonCorePlugin.getPlugin().setPythonPath(pythonText.getText());
    PythonCorePlugin.getPlugin().setPylintPath(pylintText.getText());

    IPylintConfig config = PylintConfigManager.getConfig(combo.getText());
    PythonCorePlugin.getPlugin().setPylintConfig(config);

    return true;
  }

  @Override
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayoutFactory.fillDefaults().applyTo(composite);

    Group pythonGroup = new Group(composite, SWT.NONE);
    pythonGroup.setText("Python");
    GridDataFactory.fillDefaults().grab(true, false).applyTo(pythonGroup);
    GridLayoutFactory.swtDefaults().numColumns(3).applyTo(pythonGroup);

    Label label = new Label(pythonGroup, SWT.NONE);
    label.setText("Path:");

    pythonText = new Text(pythonGroup, SWT.SINGLE | SWT.BORDER);
    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).hint(100, -1).applyTo(
        pythonText);

    Button button = new Button(pythonGroup, SWT.PUSH);
    button.setText("Browse...");
    PixelConverter converter = new PixelConverter(button);
    int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
    GridDataFactory.swtDefaults().hint(widthHint, -1).applyTo(button);
    handleBrowseButton("Python", button, pythonText);

    Group pylintGroup = new Group(composite, SWT.NONE);
    pylintGroup.setText("Pylint");
    GridDataFactory.fillDefaults().grab(true, false).applyTo(pylintGroup);
    GridLayoutFactory.swtDefaults().numColumns(3).applyTo(pylintGroup);

    label = new Label(pylintGroup, SWT.NONE);
    label.setText("Path:");

    pylintText = new Text(pylintGroup, SWT.SINGLE | SWT.BORDER);
    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).hint(100, -1).applyTo(
        pylintText);

    button = new Button(pylintGroup, SWT.PUSH);
    button.setText("Browse...");
    GridDataFactory.swtDefaults().hint(widthHint, -1).applyTo(button);
    handleBrowseButton("Pylint", button, pylintText);

    label = new Label(pylintGroup, SWT.NONE);
    label.setText("Configuration:");

    combo = new Combo(pylintGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(combo);

    pythonText.setText(nonNull(PythonCorePlugin.getPlugin().getPythonPath()));
    pylintText.setText(nonNull(PythonCorePlugin.getPlugin().getPylintPath()));

    combo.add("");

    for (IPylintConfig config : PylintConfigManager.getAllConfigs()) {
      combo.add(config.getName());
    }

    IPylintConfig config = PythonCorePlugin.getPlugin().getPylintConfig();

    if (config != null) {
      int index = combo.indexOf(config.getName());

      if (index != -1) {
        combo.select(index);
      } else {
        combo.select(0);
      }
    } else {
      combo.select(0);
    }

    return composite;
  }

  @Override
  protected void performDefaults() {
    PythonLocator locator = new PythonLocator();

    pythonText.setText(nonNull(locator.getDefaultPythonPath()));
    pylintText.setText(nonNull(locator.getDefaultPylintPath()));

    IPylintConfig defaultConfig = PylintConfigManager.getDefaultConfig();
    if (defaultConfig != null) {
      int index = combo.indexOf(defaultConfig.getName());

      if (index != -1) {
        combo.select(index);
      }
    }

    super.performDefaults();
  }

  private void handleBrowseButton(final String scriptName, Button button, final Text text) {
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        dialog.setText("Select Path to " + scriptName);
        String path = dialog.open();

        if (path != null) {
          text.setText(path);
        }
      }
    });
  }

  private String nonNull(String str) {
    return (str == null ? "" : str);
  }

}
