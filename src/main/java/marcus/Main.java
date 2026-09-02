package marcus;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Displays a JavaFX window containing a greeting.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/results.txt";
    private static final String USER_BUBBLE_STYLE = "-fx-background-color: #DCF8C6;"
            + " -fx-background-radius: 14; -fx-padding: 10;";
    private static final String MARCUS_BUBBLE_STYLE = "-fx-background-color: #F1F1F1;"
            + " -fx-background-radius: 14; -fx-padding: 10;";
    private VBox conversation;
    private ScrollPane conversationScroll;
    private TextField commandInput;
    private Marcus marcus;

    /**
     * Creates the JavaFX application.
     */
    public Main() {

    }

    @Override
    public void start(Stage stage) {
        marcus = new Marcus(DEFAULT_FILE_PATH);
        marcus.startSession();

        conversation = new VBox(8);
        conversation.setPadding(new Insets(12));
        conversation.setFillWidth(true);

        conversationScroll = new ScrollPane(conversation);
        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        conversationScroll.setStyle("-fx-background: #FFFFFF; -fx-background-color: #FFFFFF;");
        VBox.setVgrow(conversationScroll, Priority.ALWAYS);

        appendMarcusMessage("Hello, I am Marcus the Chatbot!\nWhat can I do for you?");

        Label prompt = new Label("Command:");
        commandInput = new TextField();
        commandInput.setPromptText("e.g. list, todo read a book, or bye");
        commandInput.setOnAction(event -> submitCommand());

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());

        HBox commandBar = new HBox(10, prompt, commandInput, sendButton);
        commandBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(commandInput, javafx.scene.layout.Priority.ALWAYS);

        Label title = new Label("Marcus");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        VBox content = new VBox(10, title, conversationScroll, commandBar);
        content.setPadding(new Insets(16));

        BorderPane root = new BorderPane(content);
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Marcus Chatbot");
        stage.show();
    }

    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        appendUserMessage(command);
        commandInput.clear();
        boolean shouldContinue = marcus.processCommand(command, new GuiResponseHandler());
        if (!shouldContinue) {
            commandInput.setDisable(true);
        }
    }

    private void appendUserMessage(String message) {
        appendMessage(message, USER_BUBBLE_STYLE, Pos.BOTTOM_RIGHT, false, true);
    }

    private void appendMarcusMessage(String message) {
        appendMessage(message, MARCUS_BUBBLE_STYLE, Pos.BOTTOM_LEFT, true, false);
    }

    private void appendMessage(String message, String bubbleStyle, Pos alignment,
            boolean showMarcusAvatar, boolean showUserAvatar) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMaxWidth(440);
        bubble.setStyle(bubbleStyle);

        HBox messageRow = showMarcusAvatar
                ? new HBox(8, createMarcusAvatar(), bubble)
                : showUserAvatar
                        ? new HBox(8, bubble, createUserAvatar())
                        : new HBox(bubble);
        messageRow.setMaxWidth(Double.MAX_VALUE);
        messageRow.setAlignment(alignment);
        conversation.getChildren().add(messageRow);
        Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }

    private StackPane createMarcusAvatar() {
        Circle avatarBackground = new Circle(18, Color.web("#5B6EE1"));
        Label robotIcon = new Label("🤖");
        robotIcon.setStyle("-fx-font-size: 16px;");
        return new StackPane(avatarBackground, robotIcon);
    }

    private StackPane createUserAvatar() {
        Circle avatarBackground = new Circle(18, Color.web("#8B5CF6"));
        Label userIcon = new Label("🙂");
        userIcon.setStyle("-fx-font-size: 16px;");
        return new StackPane(avatarBackground, userIcon);
    }

    private class GuiResponseHandler implements ResponseHandler {
        @Override
        public void showMessage(String message) {
            appendMarcusMessage(message);
        }

        @Override
        public void showTaskList(String taskList) {
            appendMarcusMessage(taskList);
        }

        @Override
        public void showGoodbye() {
            appendMarcusMessage("Bye. Hope to see you again soon!");
        }
    }
}
