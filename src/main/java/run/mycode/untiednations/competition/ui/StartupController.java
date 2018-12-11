package run.mycode.untiednations.competition.ui;

import java.io.File;
import run.mycode.untiednations.competition.ui.game.GameController;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import run.mycode.compiler.CompileErrorReporter;
import run.mycode.compiler.FromMemoryClassLoader;
import run.mycode.compiler.InMemoryJavaFileManager;
import run.mycode.compiler.ReportItem;
import run.mycode.untiednations.delegates.Delegate;
import run.mycode.untiednations.delegates.DelegateWrapper;

public class StartupController {

    @FXML
    private Parent frame;

    @FXML
    private ListView<Delegate> memberList;

    @FXML
    private Button removeButton;

    @FXML
    private void addClicked(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Java Source Files", "*.java"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        List<File> files
                = fileChooser.showOpenMultipleDialog(frame.getScene().getWindow());

        List<DelegateErrorReport> errors = new ArrayList<>();
        if (files != null) {
            files.forEach(file -> {
                DelegateErrorReport err = loadDelegate(file);
                if (err != null) {
                    errors.add(err);
                }
            });
        }
        
        if (!errors.isEmpty()) {
            showDelegateErrors(errors);
        }
    }

    @FXML
    private void remClicked(ActionEvent event) {
        memberList.getItems()
                .remove(memberList.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void clrClicked(ActionEvent event) {
        memberList.getItems().clear();
    }

    @FXML
    private void startClicked(ActionEvent event) {
        List<Delegate> delegates = memberList.getItems();

        Parent root;
        try {
            FXMLLoader fxl = new FXMLLoader(getClass()
                    .getResource("/fxml/GameScene.fxml"));
            root = fxl.load();

            GameController gc = fxl.<GameController>getController();
            gc.setDelegates(delegates);

            Stage compWindow = new Stage();

            compWindow.initOwner(frame.getScene().getWindow());
            compWindow.setTitle("Model UntiedNations");
            compWindow.setScene(new Scene(root));
            compWindow.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
                    ae -> gc.onClose());
            compWindow.showAndWait();
        } catch (IOException e) {
            System.err.println("Could not load competition window.");
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        memberList.setCellFactory((ListView<Delegate> list) -> new CountryCell());
    }

    public void setMembershipRoll(List<Delegate> countries) {
        memberList.setItems(FXCollections.observableArrayList(countries));

        memberList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Delegate> ov,
                        Delegate oldValue,
                        Delegate newValue) -> {
                    removeButton.disableProperty().setValue(newValue == null);
                });
    }

    private DelegateErrorReport loadDelegate(File f) {

        // Add the file to be compiled to a file list
        List<JavaFile> files = new ArrayList<>();
        files.add(new JavaFile(f.toURI()));

        // Add the name of the class to a list of classes
        String title = f.getName();
        title = title.substring(0, title.lastIndexOf("."));

        // Build the helper class instances for compiling the file
        final FromMemoryClassLoader classLoader
                = new FromMemoryClassLoader(StartupController.class.getClassLoader());
        final JavaCompiler compiler = new EclipseCompiler();
        final CompileErrorReporter diag = new CompileErrorReporter();
        final StandardJavaFileManager stdfileManager
                = compiler.getStandardFileManager(diag, Locale.ENGLISH, null);
        InMemoryJavaFileManager fileManager
                = new InMemoryJavaFileManager(stdfileManager, classLoader);

        // Prepare compiler flags
        List<String> options = new ArrayList<>();
        options.addAll(Arrays.asList("-1.8"));

        // Compile the code
        Writer out = new PrintWriter(System.out); // TODO: remove the writer, use the compile diag listener to store and display errors
        JavaCompiler.CompilationTask compileTask
                = compiler.getTask(null, fileManager, diag, options, null, files);
        boolean result = compileTask.call();

        if (diag.hasError()) {
            return new DelegateErrorReport(title, diag.getReport());
        }

        try {
            Class<?> compiledClass = classLoader.findClass(title);

            // Add an instance of the delegate to the memberList, wrapped
            // to protect from exceptions
            memberList.getItems().add(new DelegateWrapper((Delegate) compiledClass.newInstance()));
            // If there is an exception, report the exception with a "helpful" message
        } catch (ClassNotFoundException e) {
            List<ReportItem> report = new ArrayList<>();
            report.add(new ReportItem(f.getName(), true, "Class not found: "
                    + title
                    + "\nMake sure that the class does not specify a package."));
            return new DelegateErrorReport(title, report);
        } catch (InstantiationException e) {
            List<ReportItem> report = new ArrayList<>();
            report.add(new ReportItem(f.getName(), true,
                    "Could not instantiate delegate: "
                    + title
                    + "\nMake sure that the class is not abstract and has a no arg constructor."));
            return new DelegateErrorReport(title, report);
        } catch (IllegalAccessException e) {
            List<ReportItem> report = new ArrayList<>();
            report.add(new ReportItem(f.getName(), true,
                    "Could not instantiate delegate: "
                    + title
                    + "\nMake sure that the class has a public constructor."));
            return new DelegateErrorReport(title, report);
        } catch (ClassCastException e) {
            List<ReportItem> report = new ArrayList<>();
            report.add(new ReportItem(f.getName(), true,
                    "Could not cast " + title + " to Delegate."
                    + "\nMake sure that the class implements the Delegate interface."));
            return new DelegateErrorReport(title, report);
        } catch (Exception e) {
            List<ReportItem> report = new ArrayList<>();
            report.add(new ReportItem(f.getName(), true,
                    "Exception in Delegate constructor " + title + " "
                    + e.getLocalizedMessage()));
            return new DelegateErrorReport(title, report);
        }

        return null;
    }
    
    private void showDelegateErrors(List<DelegateErrorReport> errors) {
        Parent root;
        try {
            FXMLLoader fxl = new FXMLLoader(getClass()
                    .getResource("/fxml/DialogScene.fxml"));
            root = fxl.load();

            DialogController dc = fxl.<DialogController>getController();
            
            String message = "";
            
            List<ReportItem> errorItems = new ArrayList<>();
            
            for (DelegateErrorReport report : errors ) {
                if (!message.isEmpty()) {
                    message = message + "\n";
                }
                message = message + "Could not load delegate " + 
                        report.getDelegateName();
                
                report.getReport().forEach((r) -> {
                    if (r.isError()) {
                        errorItems.add(r);
                    }
                });
            }
            dc.dialogInfo(message, errorItems);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.initOwner(frame.getScene().getWindow());
            dialog.setTitle("Model UntiedNations");
            dialog.setDialogPane(fxl.getRoot());
            dialog.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Could not load error dialog.");
            e.printStackTrace();
        }
    }

    private class CountryCell extends ListCell<Delegate> {

        @Override
        protected void updateItem(Delegate item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.getCountryName());
        }
    }

    private class JavaFile extends SimpleJavaFileObject {

        public JavaFile(URI uri) {
            super(uri, JavaFileObject.Kind.SOURCE);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(this.toUri()));
            return new String(encoded);
        }
    }
}
