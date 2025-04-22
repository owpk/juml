"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.deactivate = exports.activate = void 0;
const vscode = require("vscode");
const vscode_1 = require("vscode");
const utils_1 = require("./utils");
const jdiagramName = "juml.jar";
const extName = "owpk.juml";
const drawioName = "juml.drawio";
async function activate(context) {
    let jdJarPath = await (0, utils_1.getJdPath)(extName, jdiagramName);
    console.log("jdiagrma jar path: " + jdJarPath);
    const { exec } = require("child_process");
    context.subscriptions.push(vscode_1.commands.registerCommand("juml.Source_to_Diagram", async () => {
        const selectJavaFiles = await vscode_1.window.showQuickPick(["Open File Finder", "Manually Write Path"], {
            placeHolder: "Choose JAVA Sources or directory containing them",
        });
        var javaPath = undefined;
        if (selectJavaFiles === "Open File Finder") {
            const options = {
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
        }
        else if (selectJavaFiles === "Manually Write Path") {
            javaPath = await vscode_1.window.showInputBox({
                placeHolder: "Path to JAVA source file or directory containing JAVA source files",
            });
        }
        var drawioPath = vscode.workspace.workspaceFolders
            ? vscode.workspace.workspaceFolders[0].uri.fsPath
            : ".";
        var drawioFilePath = (0, utils_1.concat)(drawioPath, drawioName);
        var command = `java -jar ${jdJarPath} -s ${javaPath} -t ${drawioFilePath}`;
        console.log("command : " + command);
        try {
            await new Promise((resolve, reject) => {
                exec(command, (err, stdout, stderr) => {
                    if (err) {
                        console.log(`Error: ${err.message}`);
                        console.log(`stderr: ${stderr}`);
                        reject(err);
                    }
                    else {
                        console.log(`stdout: ${stdout}`);
                        resolve();
                    }
                });
            });
            vscode.window.showInformationMessage(`Diagram generated successfully! ${drawioFilePath}`);
        }
        catch (error) {
            vscode.window.showErrorMessage(`Error: ${error.message}`);
        }
    }));
}
exports.activate = activate;
function deactivate() { }
exports.deactivate = deactivate;
//# sourceMappingURL=extension.js.map