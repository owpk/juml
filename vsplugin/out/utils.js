"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.concat = exports.getJdPath = void 0;
const fsPromises = require("node:fs/promises");
const path = require("node:path");
const osName = process.platform;
const userProfile = process.platform === "win32" ? "USERPROFILE" : "HOME";
const userProfilePath = process.env[userProfile];
const vscodeExtensionPath = path.join(userProfilePath, ".vscode", "extensions");
/**
 * @returns return extension absolute path ( automatically reconcile version path )
 */
async function getJdPath(extensionName, jarname) {
    let extPath = path.join(userProfilePath, ".vscode", "extensions");
    const files = await fsPromises.readdir(extPath);
    return findExtensionAbsolutePath(files, extensionName, jarname);
}
exports.getJdPath = getJdPath;
function concat(...args) {
    return args.join(path.sep);
}
exports.concat = concat;
function findExtensionAbsolutePath(files, ext, jar) {
    const extensionDir = files.find(file => file.startsWith(ext));
    if (!extensionDir) {
        throw new Error(`Extension directory starting with '${ext}' not found`);
    }
    return path.join(vscodeExtensionPath, extensionDir, jar);
}
//# sourceMappingURL=utils.js.map