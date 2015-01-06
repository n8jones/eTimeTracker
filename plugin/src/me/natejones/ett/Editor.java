package me.natejones.ett;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;



import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.google.common.collect.Iterables;

public class Editor extends EditorPart{

	private Text txtInput;
	private TableViewer tableViewer;
	private final WritableList entries = new WritableList();
	private boolean dirty = false;
	
	private void setDirty(boolean dirty){
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		FileEditorInput input = (FileEditorInput)getEditorInput();
		TimeLog log = new TimeLog();
		for(Object entry : entries) log.getLines().add(new TimeLogLine(null, (TimeLogEntry)entry));
		ByteArrayInputStream bais = new ByteArrayInputStream(TimeLogUtils.toBytes(log));
		try {
			input.getFile().setContents(bais, true, true, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		setDirty(false);
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite arg0, IEditorInput arg1)
			throws PartInitException {
		FileEditorInput input = (FileEditorInput)arg1;
		setSite(arg0);
		setInput(arg1);
		setTitle(arg1.getName());
		try {
			TimeLog timeLog = TimeLogUtils.read(input.getFile().getContents());
			entries.clear();
			for(TimeLogEntry entry : timeLog.getEntries()) entries.add(entry);
		} catch (Exception e) {
			throw new PartInitException("There was a problem loading the Timelog at: " + input, e);
		}
		
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
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
					setDirty(true);
				}
			}
		});
		GridDataFactory.defaultsFor(txtInput)
					   .grab(true, false)
					   .applyTo(txtInput);
		tableViewer = new TableViewer(parent);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		ObservableListContentProvider contentProvider = 
				new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);
		IObservableSet knownElements = contentProvider.getKnownElements();
		createPropertyColumn(tableViewer, knownElements, "time", "Time", 250);
		createPropertyColumn(tableViewer, knownElements, "message", "Message", 500);
		tableViewer.setInput(entries);
	}
	
	private static TableViewerColumn createPropertyColumn(
			TableViewer viewer, 
			IObservableSet knownElements, 
			String property, 
			String label, 
			int width){
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn tc = col.getColumn();
		tc.setWidth(width);
		tc.setText(label);
		IObservableMap oMap = BeanProperties.value(TimeLogEntry.class, property).observeDetail(knownElements);
		col.setLabelProvider(new ObservableMapCellLabelProvider(oMap));
		return col;
	}

	@Override
	public void setFocus() {
		if(txtInput != null) txtInput.forceFocus();
	}

}
