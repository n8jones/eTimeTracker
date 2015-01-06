package me.natejones.ett;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class Editor extends EditorPart{

	private Text txtInput;
	private TableViewer tableViewer;
	private final List<TimeLogEntry> entries = new Vector<>();

	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite arg0, IEditorInput arg1)
			throws PartInitException {
		FileEditorInput input = (FileEditorInput)arg1;
		setSite(arg0);
		setInput(arg1);
		setTitle(arg1.getName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("What have you been doing?");
		txtInput.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.character == '\r'){
					entries.add(new TimeLogEntry(new Date(), txtInput.getText()));
					txtInput.setText("");
					tableViewer.refresh();
				}
			}
		});
		GridDataFactory.defaultsFor(txtInput)
					   .grab(true, false)
					   .applyTo(txtInput);
		entries.add(new TimeLogEntry(new Date(), "Arrive"));
		tableViewer = new TableViewer(parent);
		final Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		createTimeColumn(tableViewer);
		createMessageColumn(tableViewer);
		
		tableViewer.setInput(entries);
	}
	
	private static void createTimeColumn(final TableViewer tableViewer){
		final TableViewerColumn dateCol = new TableViewerColumn(tableViewer, SWT.NONE);
		dateCol.getColumn().setWidth(250);
		dateCol.getColumn().setText("Time");
		dateCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			  public String getText(Object element) {
			    TimeLogEntry e = (TimeLogEntry) element;
			    Date d = e.getTime();
			    return d!=null ? d.toString() : "";
			  }
		});
	}
	
	private static void createMessageColumn(final TableViewer tableViewer){
		final TableViewerColumn dateCol = new TableViewerColumn(tableViewer, SWT.NONE);
		dateCol.getColumn().setWidth(500);
		dateCol.getColumn().setText("Message");
		dateCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			  public String getText(Object element) {
			    TimeLogEntry e = (TimeLogEntry) element;
			    return e.getMessage();
			  }
		});
	}

	@Override
	public void setFocus() {
		if(txtInput != null) txtInput.forceFocus();
	}

}
