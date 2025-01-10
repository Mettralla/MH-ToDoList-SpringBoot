package com.mindhub.todolist;

import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class TodolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodolistApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserEntityRepository userEntityRepository, TaskRepository taskRepository) {
		return args -> {
			UserEntity testUser = new UserEntity(
					"daniTejerina",
					"strongpass",
					"dani@gmail.com"
			);

			userEntityRepository.save(testUser);

			Task task1User = new Task("Walk the dog", "Take the dog for a walk", TaskStatus.PENDING);
			Task task2User = new Task("Buy groceries", "Purchase groceries for the week", TaskStatus.PENDING);
			testUser.addTask(task1User);
			testUser.addTask(task2User);

			taskRepository.save(task1User);
			taskRepository.save(task2User);

			UserEntity testUser2 = new UserEntity(
					"janeSmith",
					"securepassword",
					"jane.smith@example.com"
			);

			userEntityRepository.save(testUser2);

			Task task1User2 = new Task("Complete project", "Finish the project for the Java course", TaskStatus.PENDING);
			Task task2User2 = new Task("Clean the house", "Do the cleaning tasks in the house", TaskStatus.PENDING);
			testUser2.addTask(task1User2);
			testUser2.addTask(task2User2);

			taskRepository.save(task1User2);
			taskRepository.save(task2User2);

		};
	}
}
