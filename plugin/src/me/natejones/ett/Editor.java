package me.natejones.ett;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapCellLabelProvider;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;

public class Editor extends EditorPart {
   public Editor() {
   }

   private static final String ALL = "All";
   private static final String TODAY = "Today";
   private static final String THIS_WEEK = "This Week";
   private static final String THIS_MONTH = "This Month";
   private static final String CUSTOM = "Custom";
   private Text txtInput;
   private TableViewer tableViewer;
   private final WritableList entries = new WritableList();
   private boolean dirty = false;
   private Date startDate;
   private Date endDate;

   private void setDirty(boolean dirty) {
      this.dirty = dirty;
      firePropertyChange(PROP_DIRTY);
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      FileEditorInput input = (FileEditorInput) getEditorInput();
      TimeLog log = new TimeLog();
      for (TimeLogEntry entry : getEntries())
         log.getLines().add(new TimeLogLine(null, entry));
      ByteArrayInputStream bais = new ByteArrayInputStream(
            TimeLogUtils.toBytes(log));
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
      FileEditorInput input = (FileEditorInput) arg1;
      setSite(arg0);
      setInput(arg1);
      setPartName(arg1.getName());
      try {
         TimeLog timeLog = TimeLogUtils.read(input.getFile().getContents());
         entries.clear();
         for (TimeLogEntry entry : timeLog.getEntries())
            entries.add(entry);
      } catch (Exception e) {
         throw new PartInitException(
               "There was a problem loading the Timelog at: " + input, e);
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

   @SuppressWarnings("unused")
   @Override
   public void createPartControl(Composite parent) {
      parent.setLayout(new GridLayout(1, false));
      txtInput = new Text(parent, SWT.BORDER);
      txtInput.setMessage("What have you been doing?");
      txtInput.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent arg0) {
            if (arg0.character == '\r') {
               entries.add(new TimeLogEntry(new Date(), txtInput.getText()));
               txtInput.setText("");
               setDirty(true);
            }
         }
      });
      ContentProposalAdapter contentProposalAdapter = new ContentProposalAdapter(
            txtInput, new TextContentAdapter(), new IContentProposalProvider() {
               @Override
               public IContentProposal[] getProposals(String contents,
                     int position) {
                  final String needle = contents.toLowerCase();
                  ImmutableSortedSet<String> messages = FluentIterable
                        .from(getEntries())
                        .transform(new Function<TimeLogEntry, String>() {
                           @Override
                           public String apply(TimeLogEntry arg0) {
                              return arg0.getMessage();
                           }
                        }).filter(new Predicate<String>() {
                           @Override
                           public boolean apply(String arg0) {
                              if (Strings.isNullOrEmpty(arg0))
                                 return false;
                              String haystack = arg0.toLowerCase();
                              return haystack.contains(needle);
                           }
                        }).toSortedSet(Ordering.natural());
                  return FluentIterable.from(messages)
                        .transform(new Function<String, IContentProposal>() {
                           @Override
                           public IContentProposal apply(String arg0) {
                              return new ContentProposal(arg0);
                           }
                        }).toArray(IContentProposal.class);
               }
            }, null, null);
      contentProposalAdapter
            .setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
      GridDataFactory.defaultsFor(txtInput).grab(true, false).applyTo(txtInput);

      Group grpFilter = new Group(parent, SWT.NONE);
      grpFilter.setLayout(new GridLayout(4, false));
      grpFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
            1, 1));
      grpFilter.setText("Filter");

      final Text keywordTxt = new Text(grpFilter, SWT.BORDER | SWT.SEARCH);
      keywordTxt.setMessage("Keyword");
      keywordTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
            1, 1));

      Label lblDateRange = new Label(grpFilter, SWT.NONE);
      lblDateRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
            false, 1, 1));
      lblDateRange.setText("Date Range:");

      final Combo dateRangeType = new Combo(grpFilter, SWT.READ_ONLY);
      dateRangeType.setItems(new String[] { ALL, TODAY, THIS_WEEK, THIS_MONTH,
            CUSTOM });
      dateRangeType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
            false, 1, 1));
      dateRangeType.select(0);

      final Composite customDateRangeOptions = new Composite(grpFilter,
            SWT.NONE);
      customDateRangeOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
            false, false, 1, 1));
      customDateRangeOptions.setLayout(new GridLayout(4, false));
      customDateRangeOptions.setVisible(false);
      customDateRangeOptions.setEnabled(false);

      Label lblStartTime = new Label(customDateRangeOptions, SWT.NONE);
      lblStartTime.setText("Start Time:");

      final DateTime startDateTime = new DateTime(customDateRangeOptions,
            SWT.BORDER | SWT.DROP_DOWN);

      Label lblEndTime = new Label(customDateRangeOptions, SWT.NONE);
      lblEndTime.setText("End Time:");

      final DateTime endDateTime = new DateTime(customDateRangeOptions,
            SWT.BORDER | SWT.DROP_DOWN);

      tableViewer = new TableViewer(parent);
      Table table = tableViewer.getTable();
      table.setLayoutData(new GridData(GridData.FILL_BOTH));
      table.setHeaderVisible(true);
      table.setLinesVisible(true);
      ObservableListContentProvider contentProvider = new ObservableListContentProvider();
      tableViewer.setContentProvider(contentProvider);

      IObservableSet knownElements = contentProvider.getKnownElements();
      createPropertyColumn(tableViewer, knownElements, "time", "Time", 250);
      createPropertyColumn(tableViewer, knownElements, "message", "Message",
            500);
      tableViewer.setInput(entries);

      keywordTxt.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            tableViewer.refresh();
         }
      });
      SelectionListener dateFilterChanged = new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Editor.this.updateDateRange(dateRangeType.getText(), startDateTime,
                  endDateTime);
            tableViewer.refresh();
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            Editor.this.updateDateRange(dateRangeType.getText(), startDateTime,
                  endDateTime);
            tableViewer.refresh();
         }
      };

      startDateTime.addSelectionListener(dateFilterChanged);
      endDateTime.addSelectionListener(dateFilterChanged);

      dateRangeType.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            String drt = dateRangeType.getText();
            boolean cstmEnabled = CUSTOM.equals(drt);
            customDateRangeOptions.setVisible(cstmEnabled);
            customDateRangeOptions.setEnabled(cstmEnabled);
            Editor.this.updateDateRange(drt, startDateTime, endDateTime);
            tableViewer.refresh();
         }
      });

      tableViewer.setComparator(new ViewerComparator() {
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            TimeLogEntry tle1 = (TimeLogEntry) e1;
            TimeLogEntry tle2 = (TimeLogEntry) e2;
            return -1 * tle1.getTime().compareTo(tle2.getTime());
         }
      });

      // keyword filter
      tableViewer.addFilter(new ViewerFilter() {
         @Override
         public boolean select(Viewer viewer, Object parentElement,
               Object element) {
            if (keywordTxt.isDisposed())
               return true;
            String keyword = keywordTxt.getText();
            if (Strings.isNullOrEmpty(keyword))
               return true;
            TimeLogEntry tle = (TimeLogEntry) element;
            String message = tle.getMessage();
            return message.toLowerCase().contains(keyword.toLowerCase());
         }
      });

      tableViewer.addFilter(new ViewerFilter() {
         @Override
         public boolean select(Viewer viewer, Object parentElement,
               Object element) {
            String drt = dateRangeType.getText();
            if (ALL.equals(drt))
               return true;
            TimeLogEntry tle = (TimeLogEntry) element;
            Date time = tle.getTime();
            if (time == null)
               return false;
            if (startDate == null)
               return false;
            if (endDate == null)
               return false;
            return startDate.before(time) && endDate.after(time);
         }
      });
   }

   private void updateDateRange(String rangeType, DateTime start, DateTime end) {
      if (Strings.isNullOrEmpty(rangeType))
         return;

      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      if (TODAY.equals(rangeType)) {
         startDate = cal.getTime();
         cal.add(Calendar.HOUR, 24);
         endDate = cal.getTime();
      } else if (THIS_WEEK.equals(rangeType)) {
         cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
         startDate = cal.getTime();
         cal.add(Calendar.DATE, 7);
         endDate = cal.getTime();
      } else if (THIS_MONTH.equals(rangeType)) {
         cal.set(Calendar.DAY_OF_MONTH, 1);
         startDate = cal.getTime();
         cal.add(Calendar.MONTH, 1);
         endDate = cal.getTime();
      } else if (CUSTOM.equals(rangeType)) {
         if ((start == null) || start.isDisposed())
            return;
         if ((end == null) || end.isDisposed())
            return;
         cal.set(start.getYear(), start.getMonth(), start.getDay());
         startDate = cal.getTime();
         cal.set(end.getYear(), end.getMonth(), end.getDay(), 23, 59, 59);
         endDate = cal.getTime();
      }
   }

   private static TableViewerColumn createPropertyColumn(TableViewer viewer,
         IObservableSet knownElements, String property, String label, int width) {
      TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
      TableColumn tc = col.getColumn();
      tc.setWidth(width);
      tc.setText(label);
      IObservableMap oMap = BeanProperties.value(TimeLogEntry.class, property)
            .observeDetail(knownElements);
      col.setLabelProvider(new ObservableMapCellLabelProvider(oMap));

      return col;
   }

   @SuppressWarnings("unchecked")
   private Iterable<TimeLogEntry> getEntries() {
      return FluentIterable.from(entries).filter(TimeLogEntry.class);
   }

   @Override
   public void setFocus() {
      if (txtInput != null)
         txtInput.forceFocus();
   }

}
