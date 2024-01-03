import javafx.application.Application;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class ServerGUI extends Application {
	ListView<String> listItems = new ListView<>();
	HashMap<String, Scene> sceneMap = new HashMap<>();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Welcome to the Connect 4 Server");

		// create prompt to choose port number
		TextField tf = new TextField("Enter Port #");
		tf.setPrefWidth(250);
		tf.setPrefHeight(50);
		tf.setTranslateX(225);
		tf.setTranslateY(175);

		// create button to connect to server
		Button serverChoice = new Button("Server");
		serverChoice.setPrefWidth(150);
		serverChoice.setPrefHeight(50);
		serverChoice.setTranslateX(275);
		serverChoice.setTranslateY(300);

		serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));

		Server serverConnection = new Server(data -> Platform.runLater(()->{
			listItems.getItems().add(data.toString());

		}), Integer.parseInt(tf.getText()));});

		// create button to exit program
		Button exit = new Button("Exit");
		exit.setPrefWidth(150);
		exit.setPrefHeight(50);
		exit.setTranslateX(275);
		exit.setTranslateY(360);

		exit.setOnAction(action -> System.exit(0));

		primaryStage.setOnCloseRequest(t -> {
			Platform.exit();
			System.exit(0);
		});

		// create new scene
	    Group root = new Group(tf, serverChoice, exit);

	    Scene scene = new Scene(root, 700,700, Color.LEMONCHIFFON);
		sceneMap.put("server",  createServerGui());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public Scene createServerGui() {
		// creates new scene with listView
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(60));
		pane.setStyle("-fx-background-color: aquamarine");

		pane.setCenter(listItems);

		return new Scene(pane, 500, 500);
	}
}
