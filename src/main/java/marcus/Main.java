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
    private static final int CONVERSATION_GAP = 8;
    private static final int COMMAND_BAR_GAP = 10;
    private static final int CONTENT_PADDING = 16;
    private static final int MESSAGE_PADDING = 12;
    private static final int MESSAGE_MAX_WIDTH = 440;
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;
    private static final int AVATAR_RADIUS = 16;
    private static final String USER_BUBBLE_STYLE = "-fx-background-color: #DCF8C6;"
            + " -fx-background-radius: 16 5 16 16; -fx-padding: 10 14;"
            + " -fx-text-fill: #17301D; -fx-font-size: 13px;";
    private static final String MARCUS_BUBBLE_STYLE = "-fx-background-color: #F1F1F1;"
            + " -fx-background-radius: 5 16 16 16; -fx-padding: 11 14;"
            + " -fx-text-fill: #1F2937; -fx-font-size: 13px;";
    private static final String ERROR_BUBBLE_STYLE = "-fx-background-color: #FEE2E2;"
            + " -fx-background-radius: 5 16 16 16; -fx-padding: 11 14;"
            + " -fx-text-fill: #991B1B; -fx-font-size: 13px;";
    private static final String ROOT_STYLE = "-fx-background-color: #F6F8FC;";
    private static final String HEADER_STYLE = "-fx-background-color: #FFFFFF;"
            + " -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1 0;";
    private static final String INPUT_STYLE = "-fx-background-color: #FFFFFF;"
            + " -fx-border-color: #D1D5DB; -fx-border-radius: 9; -fx-background-radius: 9;"
            + " -fx-padding: 9 12; -fx-font-size: 13px;";
    private static final String SEND_BUTTON_STYLE = "-fx-background-color: #4F46E5;"
            + " -fx-text-fill: white; -fx-background-radius: 9; -fx-padding: 9 18;"
            + " -fx-font-weight: bold;";
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

        conversation = new VBox(CONVERSATION_GAP);
        conversation.setPadding(new Insets(MESSAGE_PADDING, 10, MESSAGE_PADDING, 10));
        conversation.setFillWidth(true);

        conversationScroll = new ScrollPane(conversation);
        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        conversationScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(conversationScroll, Priority.ALWAYS);

        appendMarcusMessage("Hello, I am Marcus the Chatbot!\nWhat can I do for you?");

        Label prompt = new Label("Ask Marcus");
        prompt.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px; -fx-font-weight: bold;");
        commandInput = new TextField();
        commandInput.setPromptText("e.g. list, todo read a book, or bye");
        commandInput.setStyle(INPUT_STYLE);
        commandInput.setOnAction(event -> submitCommand());

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle(SEND_BUTTON_STYLE);
        sendButton.setOnAction(event -> submitCommand());

        HBox commandBar = new HBox(COMMAND_BAR_GAP, prompt, commandInput, sendButton);
        commandBar.setAlignment(Pos.CENTER_LEFT);
        commandBar.setPadding(new Insets(10, 12, 12, 12));
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        Label title = new Label("Marcus");
        title.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label subtitle = new Label("Your personal task manager");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
        VBox headerText = new VBox(2, title, subtitle);
        HBox header = new HBox(10, createMarcusAvatar(), headerText);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, CONTENT_PADDING, 11, CONTENT_PADDING));
        header.setStyle(HEADER_STYLE);

        VBox content = new VBox(0, header, conversationScroll, commandBar);
        content.setStyle(ROOT_STYLE);

        BorderPane root = new BorderPane(content);
        root.setStyle(ROOT_STYLE);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Marcus Chatbot");
        stage.setResizable(true);
        stage.setMinWidth(420);
        stage.setMinHeight(360);
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
        appendMessage(message, USER_BUBBLE_STYLE, Pos.BOTTOM_RIGHT, true);
    }

    private void appendMarcusMessage(String message) {
        appendMessage(message, MARCUS_BUBBLE_STYLE, Pos.BOTTOM_LEFT, false);
    }

    private void appendErrorMessage(String message) {
        appendMessage(message, ERROR_BUBBLE_STYLE, Pos.BOTTOM_LEFT, false);
    }

    private void appendMessage(String message, String bubbleStyle, Pos alignment,
            boolean isUserMessage) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMinWidth(0);
        bubble.setMaxWidth(MESSAGE_MAX_WIDTH);
        conversation.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double availableWidth = Math.max(200, newWidth.doubleValue() - 20);
            double preferredWidth = availableWidth * (isUserMessage ? 0.82 : 0.92);
            bubble.setMaxWidth(Math.min(MESSAGE_MAX_WIDTH, preferredWidth));
        });
        bubble.setStyle(bubbleStyle);

        HBox messageRow = isUserMessage
                ? new HBox(8, bubble, createUserAvatar())
                : new HBox(8, createMarcusAvatar(), bubble);
        messageRow.setMaxWidth(Double.MAX_VALUE);
        messageRow.setAlignment(alignment);
        conversation.getChildren().add(messageRow);
        double availableWidth = Math.max(200, conversation.getWidth() - 20);
        double preferredWidth = availableWidth * (isUserMessage ? 0.82 : 0.92);
        bubble.setMaxWidth(Math.min(MESSAGE_MAX_WIDTH, preferredWidth));
        Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }

    private StackPane createMarcusAvatar() {
        Circle avatarBackground = new Circle(AVATAR_RADIUS, Color.web("#5B6EE1"));
        Label robotIcon = new Label("🤖");
        robotIcon.setStyle("-fx-font-size: 14px;");
        return new StackPane(avatarBackground, robotIcon);
    }

    private StackPane createUserAvatar() {
        Circle avatarBackground = new Circle(AVATAR_RADIUS, Color.web("#8B5CF6"));
        Label userIcon = new Label("🙂");
        userIcon.setStyle("-fx-font-size: 14px;");
        return new StackPane(avatarBackground, userIcon);
    }

    private class GuiResponseHandler implements ResponseHandler {
        @Override
        public void showMessage(String message) {
            appendMarcusMessage(message);
        }

        @Override
        public void showError(String message) {
            appendErrorMessage(message);
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
