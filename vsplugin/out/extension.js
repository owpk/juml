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
        const selectJavaFiles = await vscode_1.window.showQuickPick(["Select From Project Root", "Browse Other Location"], {
            placeHolder: "Choose Java source location",
        });
        var javaPath = "./";
        if (selectJavaFiles === "Select From Project Root") {
            const workspaceFolders = vscode.workspace.workspaceFolders;
            if (workspaceFolders && workspaceFolders.length > 0) {
                const folderPaths = workspaceFolders.map(folder => ({
                    label: folder.name,
                    description: folder.uri.fsPath
                }));
                const selectedFolder = await vscode_1.window.showQuickPick(folderPaths, {
                    placeHolder: "Select project directory containing Java sources",
                });
                if (selectedFolder) {
                    javaPath = selectedFolder.description;
                    console.log("Selected project directory: " + javaPath);
                }
            }
            else {
                vscode.window.showWarningMessage("No workspace folders found. Please open a project first.");
            }
        }
        else if (selectJavaFiles === "Browse Other Location") {
            const options = {
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
        var drawioPath = (0, utils_1.getTempDir)();
        var drawioFilePath = (0, utils_1.concat)(drawioPath, drawioName);
        var command = `java -jar ${jdJarPath} -s ${javaPath} -t ${drawioFilePath}`;
        console.log("command : " + command);
        await new Promise((resolve, reject) => {
            exec(command, (err, stdout, stderr) => {
                if (err) {
                    console.log(`Error: ${err.message}`);
                    console.log(`stderr: ${stderr}`);
                    vscode.window.showErrorMessage(`Error: ${err.message}`);
                    reject(err);
                }
                else {
                    vscode.commands.executeCommand('vscode.open', vscode.Uri.file(drawioFilePath));
                    console.log(`stdout: ${stdout}`);
                    vscode.window.showInformationMessage(`Diagram generated successfully! ${drawioFilePath}`);
                    resolve();
                }
            });
        });
    }));
}
exports.activate = activate;
function deactivate() { }
exports.deactivate = deactivate;
//# sourceMappingURL=extension.js.map