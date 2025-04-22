import * as fsPromises from "node:fs/promises";
import * as path from "node:path";

const osName = process.platform;
const userProfile = osName === "win32" ? "USERPROFILE" : "HOME";
const tempDir = osName === "win32" ? process.env.TEMP : "/tmp";
const userProfilePath = process.env[userProfile];
const vscodeExtensionPath = path.join(userProfilePath, ".vscode", "extensions");

/**
 * @returns return extension absolute path ( automatically reconcile version path )
 */
export async function getJdPath(extensionName: string, jarname: string): Promise<string> {
  let extPath = path.join(userProfilePath, ".vscode", "extensions");
  const files = await fsPromises.readdir(extPath);
  return findExtensionAbsolutePath(files, extensionName, jarname);
}

export function concat(...args: string[]): string {
  return args.join(path.sep);
}

export function getTempDir(): string {
  return tempDir;
}

function findExtensionAbsolutePath(files: string[], ext: string, jar: string): string {
  const extensionDir = files.find(file => file.startsWith(ext));

  if (!extensionDir) {
    throw new Error(`Extension directory starting with '${ext}' not found`);
  }

  return path.join(vscodeExtensionPath, extensionDir, jar);
}
