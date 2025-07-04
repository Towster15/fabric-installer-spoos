/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.installer.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import mjson.Json;

import net.fabricmc.installer.util.Utils;

public class ProfileInstaller {
	private final Path mcDir;

	public ProfileInstaller(Path mcDir) {
		this.mcDir = mcDir;
	}

	public List<LauncherType> getInstalledLauncherTypes() {
		return Arrays.stream(LauncherType.values())
				.filter(launcherType -> Files.exists(mcDir.resolve(launcherType.profileJsonName)))
				.collect(Collectors.toList());
	}

	public void setupProfile(String name, String gameVersion, LauncherType launcherType) throws IOException {
		Path launcherProfiles = mcDir.resolve(launcherType.profileJsonName);

		if (!Files.exists(launcherProfiles)) {
			throw new FileNotFoundException("Could not find " + launcherType.profileJsonName);
		}

		System.out.println("Creating profile");

		Json jsonObject = Json.read(Utils.readString(launcherProfiles));

		Json profiles = jsonObject.at("profiles");

		if (profiles == null) {
			profiles = Json.object();
			jsonObject.set("profiles", profiles);
		}

		String profileName = "Spoosaroo 2025 - " + gameVersion;

		Json profile = profiles.at(profileName);

		if (profile == null) {
			profile = createProfile(profileName);
			profiles.set(profileName, profile);
		}

		profile.set("lastVersionId", name);

		Utils.writeToFile(launcherProfiles, jsonObject.toString());

		// Create the mods directory
		Path modsDir = mcDir.resolve("mods");

		if (Files.notExists(modsDir)) {
			Files.createDirectories(modsDir);
		}

		if (Files.isDirectory(modsDir)) {
			try (DirectoryStream<Path> directory = Files.newDirectoryStream(modsDir)) {
				if (directory.iterator().hasNext()) {
					JOptionPane.showMessageDialog(null, "Mods directory is "
							+ "not empty - you may have modded the game before and will need to clear "
							+ "your mods to prevent issues.");
				}
			} catch (IOException ioEx) {
				JOptionPane.showMessageDialog(null, "Error checking if mods "
						+ "directory is empty.");
			}
		}
	}

	private static Json createProfile(String name) {
		Json jsonObject = Json.object();
		jsonObject.set("name", name);
		jsonObject.set("type", "custom");
		jsonObject.set("created", Utils.ISO_8601.format(new Date()));
		jsonObject.set("lastUsed", Utils.ISO_8601.format(new Date()));
		jsonObject.set("icon", Utils.getProfileIcon());
		jsonObject.set("javaArgs", "-Xmx4G -XX:+UnlockExperimentalVMOptions "
				+ "-XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20"
				+ " -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M"
				+ "-javaagent:unsup.jar");
		return jsonObject;
	}

	public enum LauncherType {
		WIN32("launcher_profiles.json"),
		MICROSOFT_STORE("launcher_profiles_microsoft_store.json");

		public final String profileJsonName;

		LauncherType(String profileJsonName) {
			this.profileJsonName = profileJsonName;
		}
	}
}
