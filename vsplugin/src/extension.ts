import * as vscode from "vscode";
import { commands, window } from "vscode";
import { concat, getJdPath, getTempDir } from "./utils";

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
        ["Select From Project Root", "Browse Other Location"],
        {
          placeHolder: "Choose Java source location",
        }
      );

      var javaPath = "./";

      if (selectJavaFiles === "Select From Project Root") {
        const workspaceFolders = vscode.workspace.workspaceFolders;

        if (workspaceFolders && workspaceFolders.length > 0) {
          const folderPaths = workspaceFolders.map(folder => ({
            label: folder.name,
            description: folder.uri.fsPath
          }));

          const selectedFolder = await window.showQuickPick(folderPaths, {
            placeHolder: "Select project directory containing Java sources",
          });

          if (selectedFolder) {
            javaPath = selectedFolder.description;
            console.log("Selected project directory: " + javaPath);
          }
        } else {
          vscode.window.showWarningMessage("No workspace folders found. Please open a project first.");
        }
      } else if (selectJavaFiles === "Browse Other Location") {
        const options: vscode.OpenDialogOptions = {
          canSelectMany: false,
          openLabel: "Select",
          canSelectFiles: true,
          canSelectFolders: true,
        };

        const fileUri = await vscode.window.showOpenDialog(options);
        if (fileUri && fileUri[0]) {
          javaPath = fileUri[0].fsPath;
          console.log("Selected location: " + javaPath);
        }
      }

      var drawioPath: string = getTempDir();
      var drawioFilePath: string = concat(drawioPath, drawioName);
      var command = `java -jar ${jdJarPath} -s ${javaPath} -t ${drawioFilePath}`;
      console.log("command : " + command);

      await new Promise<void>((resolve, reject) => {
        exec(command, (err: any, stdout: any, stderr: any) => {
          if (err) {
            console.log(`Error: ${err.message}`);
            console.log(`stderr: ${stderr}`);
            vscode.window.showErrorMessage(`Error: ${err.message}`);
            reject(err);
          } else {
            vscode.commands.executeCommand('vscode.open', vscode.Uri.file(drawioFilePath));
            console.log(`stdout: ${stdout}`);
            vscode.window.showInformationMessage(
              `Diagram generated successfully! ${drawioFilePath}`,
            );
            resolve();
          }
        });
      });
    }),
  );
}

export function deactivate() { }
