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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import run.mycode.compiler.CompileDiagnosticListener;
import run.mycode.compiler.FromMemoryClassLoader;
import run.mycode.compiler.InMemoryJavaFileManager;
import run.mycode.untiednations.delegates.Delegate;

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
        if (files != null) {
            files.forEach(file -> loadDelegate(file));
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

    private void loadDelegate(File f) {
        List<JavaFile> files = new ArrayList<>();
        files.add(new JavaFile(f.toURI()));
        
        String title = f.getName();
        title = title.substring(0, title.lastIndexOf("."));

        final FromMemoryClassLoader classLoader = 
                new FromMemoryClassLoader(StartupController.class.getClassLoader());
        final JavaCompiler compiler = new EclipseCompiler();
        final CompileDiagnosticListener diag = new CompileDiagnosticListener();
        final StandardJavaFileManager stdfileManager = 
                compiler.getStandardFileManager(diag, Locale.ENGLISH, null);

        InMemoryJavaFileManager fileManager = 
                new InMemoryJavaFileManager(stdfileManager, classLoader);
        
        List<String> options = new ArrayList<>();
        options.addAll(Arrays.asList("-1.8"));
        
        Writer out = new PrintWriter(System.out);
        JavaCompiler.CompilationTask task = 
                compiler.getTask(out, fileManager, diag, options, null, files);

        boolean result = task.call();
        
        try {
            Class<?> compiledClass = classLoader.findClass(title);
            
            if (Delegate.class.isAssignableFrom(compiledClass)) {
               memberList.getItems().add((Delegate)compiledClass.newInstance());
            }
            else {
                System.out.println(compiledClass.getCanonicalName() + " is not a subclass of Delegate.");
            }
        }
        catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + title);
        } catch (InstantiationException | IllegalAccessException ex) {
            System.out.println("Could not instantiate delegate: " + title);
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
