package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import application.modeles.Employe;
import application.views.UI_Controller;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/application/views/MainLayout.fxml"));
			UI_Controller.setUtilisateur(
				new Employe(0, "Akrem", "Medimagh", "cool_chair_1", "Password132", "employe")
			);
			Scene scene = new Scene(root);
			
			primaryStage.setTitle("Application Pharmacie");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
