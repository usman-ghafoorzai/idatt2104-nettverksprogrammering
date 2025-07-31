package edu.ntnu.npoving5backend.controller;

import org.springframework.web.bind.annotation.*;
import java.io.*;

@RestController
@RequestMapping("/api")
public class CodeRunnerController {

  @PostMapping("/run")
  public String runCode(@RequestBody CodeRequest request) {
    String code = request.getCode();
    String filename = "Main.java";
    String output;

    try {
      // Save the code to a file
      try (FileWriter writer = new FileWriter(filename)) {
        writer.write(code);
      }

      // Compile the code in Docker container
      Process compileProcess = new ProcessBuilder(
          "docker", "run", "--rm", "-v", System.getProperty("user.dir") + ":/usr/src/app",
          "-w", "/usr/src/app", "java-runner", "javac", filename
      ).start();
      compileProcess.waitFor();

      //  Run the code in Docker container
      Process runProcess = new ProcessBuilder(
          "docker", "run", "--rm", "-v", System.getProperty("user.dir") + ":/usr/src/app",
          "-w", "/usr/src/app", "java-runner", "java", "Main"
      ).start();

      //  Get output from the program
      output = new String(runProcess.getInputStream().readAllBytes());

    } catch (IOException | InterruptedException e) {
      return "Error: " + e.getMessage(); // Handle errors and return as response
    }

    return output; // Send back output to frontend
  }
}

// Data class for JSON request from frontend
class CodeRequest {
  private String code;
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
}