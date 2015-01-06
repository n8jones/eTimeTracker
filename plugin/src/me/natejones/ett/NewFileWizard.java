package me.natejones.ett;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewFileWizard extends BasicNewResourceWizard{
	private WizardNewFileCreationPage mainPage;
	
	@Override
	public void addPages() {
		super.addPages();
		mainPage = new WizardNewFileCreationPage("newTimeLogPage1", getSelection());
		mainPage.setFileExtension("timelog");
		mainPage.setFileName("timelog.timelog");
      	addPage(mainPage);
	}
	
	@Override
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		return file != null;
	}

}
