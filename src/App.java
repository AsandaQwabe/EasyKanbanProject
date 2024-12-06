import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    // array list to store tasks dynamically
    public static List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome to EasyKanban");//the welcome message and app name

        // this prompts user for login details, i have added specific requirements for both username and password
        String userName = JOptionPane.showInputDialog(null, "Enter username:");
        String password = JOptionPane.showInputDialog(null, "Enter your password:");

        // instance of Login class
        Login login = new Login(userName, password);

        if (login.isUserNameComplex() && login.isPasswordComplex()) {
            // Shows menu and processes user's choice using a switch
            int choice;
            do {
                choice = showMainMenu();
                switch (choice) {
                    case 1:
                        addTasks();
                        break;
                    case 2:
                        showReport();
                        break;
                    case 3:
                        JOptionPane.showMessageDialog(null, "Goodbye! Exiting the app.");
                        break;
                    case 4:
                        displayTasksWithStatusDone();
                        break;
                    case 5:
                        displayTaskWithLongestDuration();
                        break;
                    case 6:
                        searchTaskByName();
                        break;
                    case 7:
                        searchTasksByDeveloper();
                        break;
                    case 8:
                        deleteTaskByName();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Incorrect choice. Please try again.");
                        break;
                }
            } while (choice != 3);
        } else {
            JOptionPane.showMessageDialog(null, "Username or password is incorrect. Please try again.");
        }
    }

    public static int showMainMenu() {
        String choiceStr = JOptionPane.showInputDialog(
                "Main Menu:\n" +
                        "1) Add tasks\n" +  //here users can add tasks, 1 or more
                        "2) Show report\n" + //this shows all tasks put in by a user in that specific session
                        "3) Quit\n" + // exit app
                        "4) Display tasks with status 'Done'\n" + //here you can view tasks that are done
                        "5) Display task with longest duration\n" +  //here you can view tasks with the longest duaration
                        "6) Search task by name\n" + //allows user to search tasks by name
                        "7) Search tasks by developer\n" + //allows yours to search by developer first name and last name
                        "8) Delete a task by name\n\n" + //users can delete a tasks by its task name
                        "Enter your choice (1-8):");
        try {
            return Integer.parseInt(choiceStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void addTasks() {
        String numTasksStr = JOptionPane.showInputDialog("Enter the number of tasks to enter:");
        try {
            int numTasks = Integer.parseInt(numTasksStr);
            int totalHours = 0;

            for (int i = 0; i < numTasks; i++) {
                Task task = new Task();

                task.taskName = JOptionPane.showInputDialog("Enter the task name:");
                task.taskDescription = JOptionPane.showInputDialog("Enter the task description (max 50 characters):");
                task.firstName = JOptionPane.showInputDialog("Enter the developer's first name:");
                task.lastName = JOptionPane.showInputDialog("Enter the developer's last name:");
                String durationStr = JOptionPane.showInputDialog("Enter the task duration in hours:");
                task.taskDuration = Integer.parseInt(durationStr);

                if (!task.checkTaskDescription()) {
                    JOptionPane.showMessageDialog(null, "Task description is too long. Try again.");
                    i--; // Retry current task
                    continue;
                }

                String[] statusOptions = {"To Do", "Done", "Doing"};
                int statusChoice = JOptionPane.showOptionDialog(null,
                        "Select task status:", "Task Status",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, statusOptions, statusOptions[0]);
                task.taskStatus = statusOptions[statusChoice];

                task.taskID = task.createTaskID(i);
                tasks.add(task);
                totalHours += task.taskDuration;

                JOptionPane.showMessageDialog(null, task.printTaskDetails());
            }

            JOptionPane.showMessageDialog(null, "Total hours for task(s): " + totalHours);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number entered. Returning to menu.");
        }
    }

    public static void showReport() {
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tasks found.");
            return;
        }

        StringBuilder reportBuilder = new StringBuilder("Task Report:\n\n");
        for (Task task : tasks) {
            reportBuilder.append(task.printTaskDetails()).append("\n");
        }

        JOptionPane.showMessageDialog(null, reportBuilder.toString());
    }

    public static void displayTasksWithStatusDone() {
        StringBuilder resultBuilder = new StringBuilder("Tasks with status 'Done':\n\n");
        for (Task task : tasks) {
            if ("Done".equals(task.taskStatus)) {
                resultBuilder.append(task.printTaskDetails()).append("\n");
            }
        }
        if (resultBuilder.length() == 0) {
            resultBuilder.append("No tasks with status 'Done' found.\n");
        }
        JOptionPane.showMessageDialog(null, resultBuilder.toString());
    }

    public static void displayTaskWithLongestDuration() {
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tasks found.");
            return;
        }

        Task longestTask = tasks.stream().max((t1, t2) -> t1.taskDuration - t2.taskDuration).orElse(null);
        JOptionPane.showMessageDialog(null, "Task with the longest duration:\n\n" + longestTask.printTaskDetails());
    }

    public static void searchTaskByName() {
        String searchName = JOptionPane.showInputDialog("Enter the task name to search:");
        for (Task task : tasks) {
            if (task.taskName.equalsIgnoreCase(searchName)) {
                JOptionPane.showMessageDialog(null, "Task found:\n\n" + task.printTaskDetails());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No tasks with the name found.");
    }

    public static void searchTasksByDeveloper() {
        String searchDeveloper = JOptionPane.showInputDialog("Enter developer's name to search:");
        StringBuilder resultBuilder = new StringBuilder("Tasks developed by: ").append(searchDeveloper).append("\n\n");
        for (Task task : tasks) {
            if ((task.firstName + " " + task.lastName).equalsIgnoreCase(searchDeveloper)) {
                resultBuilder.append(task.printTaskDetails()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, resultBuilder.length() > 0 ? resultBuilder.toString() : "No tasks found.");
    }

    public static void deleteTaskByName() {
        String deleteName = JOptionPane.showInputDialog("Enter task name to delete:");
        tasks.removeIf(task -> task.taskName.equalsIgnoreCase(deleteName));
        JOptionPane.showMessageDialog(null, "Task deleted (if it existed).");
    }
}

// Login Class
class Login {
    String userName;
    String password;

    public Login(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public boolean isUserNameComplex() {
        return userName.matches("^[a-zA-Z][a-zA-Z0-9]{4,}$");
    }

    public boolean isPasswordComplex() {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }
}

// Task Class
class Task {
    String taskName;
    String taskDescription;
    String firstName;
    String lastName;
    String taskID;
    String taskStatus;
    int taskDuration;

    public boolean checkTaskDescription() {
        return taskDescription.length() <= 50;
    }

    public String createTaskID(int taskNumber) {
        return taskName.substring(0, 2).toUpperCase() + ":" + taskNumber + ":" + lastName.toUpperCase();
    }

    public String printTaskDetails() {
        return "Task ID: " + taskID +
                "\nTask Name: " + taskName +
                "\nDescription: " + taskDescription +
                "\nDeveloper: " + firstName + " " + lastName +
                "\nDuration: " + taskDuration + " hours" +
                "\nStatus: " + taskStatus;
    }
}
