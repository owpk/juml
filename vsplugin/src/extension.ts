import * as vscode from "vscode";
import { commands, window } from "vscode";
import { concat, getJdPath } from "./utils";

const jdiagramName = "juml.jar";
const extName = "owpk.juml";
const drawioName = "juml.drawio";

export async function activate(context: vscode.ExtensionContext) {
  let jdJarPath = await getJdPath(extName, jdiagramName);
  console.log("jdiagrma jar path: " + jdJarPath);

  const { exec } = require("child_process");

  context.subscriptions.push(
    commands.registerCommand("juml.Source_to_Diagram", async () => {
      const selectJavaFiles = await window.showQuickPick(
        ["Open File Finder", "Manually Write Path"],
        {
          placeHolder: "Choose JAVA Sources or directory containing them",
        },
      );
      var javaPath = undefined;
      if (selectJavaFiles === "Open File Finder") {
        const options: vscode.OpenDialogOptions = {
          canSelectMany: false,
          openLabel: "Select",
          canSelectFiles: true,
          canSelectFolders: true,
        };

        await vscode.window.showOpenDialog(options).then((fileUri) => {
          if (fileUri && fileUri[0]) {
            console.log("Selected file: " + fileUri[0].fsPath);
            javaPath = fileUri[0].fsPath;
          }
        });
      } else if (selectJavaFiles === "Manually Write Path") {
        javaPath = await window.showInputBox({
          placeHolder:
            "Path to JAVA source file or directory containing JAVA source files",
        });
      }

      var drawioPath: string = vscode.workspace.workspaceFolders
        ? vscode.workspace.workspaceFolders[0].uri.fsPath
        : ".";
      var drawioFilePath: string = concat(drawioPath, drawioName);
      var command = `java -jar ${jdJarPath} -s ${javaPath} -t ${drawioFilePath}`;
      console.log("command : " + command);

      try {
        await new Promise<void>((resolve, reject) => {
          exec(command, (err: any, stdout: any, stderr: any) => {
            if (err) {
              console.log(`Error: ${err.message}`);
              console.log(`stderr: ${stderr}`);
              reject(err);
            } else {
              console.log(`stdout: ${stdout}`);
              resolve();
            }
          });
        });
        vscode.window.showInformationMessage(
          `Diagram generated successfully! ${drawioFilePath}`,
        );
      } catch (error: any) {
        vscode.window.showErrorMessage(`Error: ${error.message}`);
      }
    }),
  );
}

export function deactivate() {}
