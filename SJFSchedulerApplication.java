package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SJFSchedulerApplication extends Application {

	@Override
	public void start(Stage primaryStage) {
		Scanner sc = new Scanner(System.in);
		char restart;
		do {
			System.out.println("------------------------------");
			System.out.println("   Shortest Job First (SJF)   ");
			System.out.println("------------------------------");
			int numProcesses = 0;
			boolean validInput = false;

			while (!validInput) {
				try {
					System.out.print("Enter the number of processes: ");
					numProcesses = sc.nextInt();
					validInput = true;
				} catch (InputMismatchException e) {
					System.out.println();
					System.out.println("Please enter a numbers only.");
					sc.nextLine();
				}
			}

			int[] arrivalTimes = new int[numProcesses];
			int[] burstTimes = new int[numProcesses];
			int[] completionTimes = new int[numProcesses];
			int[] waitingTimes = new int[numProcesses];
			int[] turnaroundTimes = new int[numProcesses];

			for (int i = 0; i < numProcesses; i++) {
				int arrivalTime;
				int burstTime;
				boolean validArrivalInput = false;
				boolean validBurstInput = false;

				while (!validArrivalInput) {
					try {
						System.out.print("Enter Arrival Time for Process " + (i + 1) + ": ");
						arrivalTime = sc.nextInt();
						validArrivalInput = true;
						arrivalTimes[i] = arrivalTime;
					} catch (InputMismatchException e) {
						System.out.println();
						System.out.println("Please enter a numbers only.");
						sc.nextLine();
					}
				}

				while (!validBurstInput) {
					try {
						System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
						burstTime = sc.nextInt();
						validBurstInput = true;
						burstTimes[i] = burstTime;
					} catch (InputMismatchException e) {
						System.out.println();
						System.out.println("Please enter a numbers only.");
						sc.nextLine();
					}
				}
			}

			int currentTime = 0;
			int completedProcesses = 0;
			while (completedProcesses < numProcesses) {
				int shortestIndex = getShortestJobIndex(arrivalTimes, burstTimes, currentTime);

				if (shortestIndex == -1) {
					currentTime++;
				} else {
					burstTimes[shortestIndex]--;
					currentTime++;

					if (burstTimes[shortestIndex] == 0) {
						completedProcesses++;
						completionTimes[shortestIndex] = currentTime;
						turnaroundTimes[shortestIndex] = completionTimes[shortestIndex] - arrivalTimes[shortestIndex];
						waitingTimes[shortestIndex] = turnaroundTimes[shortestIndex] - burstTimes[shortestIndex];
					}
				}
			}

			displayProcessTable(numProcesses, arrivalTimes, burstTimes, completionTimes, turnaroundTimes, waitingTimes);

			float totalTime = currentTime;
			float averageWaitingTime = (float) Arrays.stream(waitingTimes).average().orElse(0);
			float averageTurnaroundTime = (float) Arrays.stream(turnaroundTimes).average().orElse(0);

			displayAverageTimes(totalTime, averageWaitingTime, averageTurnaroundTime);

			launchGanttChartWindow(primaryStage, completionTimes, (int) totalTime, averageWaitingTime,
					averageTurnaroundTime);

			System.out.println();
			System.out.print("Process completed. Do you want to restart? (Y/N): ");
			restart = Character.toLowerCase(sc.next().charAt(0));

			while (restart != 'y' && restart != 'n') {
				System.out.println("Please enter a valid answer: ");
				restart = Character.toLowerCase(sc.next().charAt(0));
			}
		} while (restart == 'y');

		System.out.println();
		System.out.println("Showing the Gantt Chart Table in a second...");
		System.out.println("");
		System.out.println("Created by Ezekiel A. Bayan | From: BSCpE-2A");
		System.out.println("Task Performance in Operating System (Finals)");

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		sc.close();
	}

	private int getShortestJobIndex(int[] arrivalTimes, int[] burstTimes, int currentTime) {
		int shortestTime = Integer.MAX_VALUE;
		int shortestIndex = -1;

		for (int i = 0; i < arrivalTimes.length; i++) {
			if (arrivalTimes[i] <= currentTime && burstTimes[i] < shortestTime && burstTimes[i] > 0) {
				shortestTime = burstTimes[i];
				shortestIndex = i;
			}
		}
		return shortestIndex;
	}

	private void displayProcessTable(int numProcesses, int[] arrivalTimes, int[] burstTimes, int[] completionTimes,
			int[] turnaroundTimes, int[] waitingTimes) {
		System.out.println();
		System.out.println("Process Table:");
		System.out.println("|------------------------------------------------------------|");
		System.out.println("| pid | Arrival Time | Burst Time | Completed Time | TA | WT |");
		System.out.println("|------------------------------------------------------------|");
		for (int i = 0; i < numProcesses; i++) {
			System.out.printf("| %-3d | %-12d | %-10d | %-14d | %-2d | %-2d |%n", i + 1, arrivalTimes[i],
					burstTimes[i], completionTimes[i], turnaroundTimes[i], waitingTimes[i]);
		}
		System.out.println("|------------------------------------------------------------|");
	}

	private void displayAverageTimes(float totalTime, float averageWaitingTime, float averageTurnaroundTime) {
		System.out.println();
		System.out.printf("The Total Time is: %.0f%n", totalTime);
		System.out.println();
		System.out.printf("The Average Waiting Time is: %.2fms%n", averageWaitingTime);
		System.out.printf("The Average Turnaround Time is: %.2fms%n", averageTurnaroundTime);
	}

	private void launchGanttChartWindow(Stage primaryStage, int[] completionTimes, int totalTime,
			float averageWaitingTime, float averageTurnaroundTime) {
		Pane root = new Pane();
		double scale = 50.0;

		Text title = new Text("Gantt Chart Table (SJF)");
		title.setFont(new Font("Trebuchet MS", 20));
		title.setX(30);
		title.setY(30);

		Text totalTimeText = new Text("The Total Time is: " + totalTime);
		totalTimeText.setFont(new Font("Trebuchet MS", 12));
		totalTimeText.setX(30);
		totalTimeText.setY(120);

		Text avgWaitingTimeText = new Text("The Average WT is: " + String.format("%.2fms", averageWaitingTime));
		avgWaitingTimeText.setFont(new Font("Trebuchet MS", 12));
		avgWaitingTimeText.setX(30);
		avgWaitingTimeText.setY(140);

		Text avgTurnaroundTimeText = new Text(
				"The Average TA is: " + String.format("%.2fms", averageTurnaroundTime));
		avgTurnaroundTimeText.setFont(new Font("Trebuchet MS", 12));
		avgTurnaroundTimeText.setX(30);
		avgTurnaroundTimeText.setY(160);

		root.getChildren().addAll(title, totalTimeText, avgWaitingTimeText, avgTurnaroundTimeText);

		double xPos = 0;
		for (int i = 0; i < completionTimes.length; i++) {
			double width = (i == 0 ? completionTimes[i] : completionTimes[i] - completionTimes[i - 1]) * scale;

			Rectangle rect = new Rectangle(xPos, 50, width, 20);
			rect.setStroke(Color.BLACK);
			rect.setFill(Color.TRANSPARENT);

			Text text = new Text(xPos + 5, 65, "P" + (i + 1));

			root.getChildren().addAll(rect, text);
			xPos += width;
		}

		for (int i = 0; i <= totalTime; i++) {
			Text timeText = new Text(scale * i - (i < 10 ? 3 : 7), 85, String.valueOf(i));
			root.getChildren().add(timeText);
		}

		ScrollPane scrollPane = new ScrollPane(root);
		scrollPane.setPrefViewportWidth(700);
		scrollPane.setPrefViewportHeight(400);
		scrollPane.setPannable(true);

		primaryStage.setTitle("Gantt Chart of Shortest Job First");
		primaryStage.setScene(new Scene(scrollPane));
		primaryStage.show();
	}
}