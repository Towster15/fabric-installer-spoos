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

package com.towster15;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.swing.JOptionPane;

public class UnsupInstaller {
	protected static final String JAR_URL = "https://git.sleeping"
			+ ".town/unascribed/unsup/releases/download/v1.1.4/unsup-1.1.4.jar";
	protected static final String INI_URL = "https://spoosaroo.com/pack/unsup.ini";

	public static void installJar(Path mcPath) {
		downloadAndInstallFile(JAR_URL, mcPath, "/unsup.jar");
	}

	public static void installIni(Path mcPath) {
		downloadAndInstallFile(INI_URL, mcPath, "/unsup.ini");
	}

	protected static void downloadAndInstallFile(String url, Path mcPath, String filename) {
		try (BufferedInputStream in =
					new BufferedInputStream(new URL(url).openStream());
				FileOutputStream fileOutputStream =
						new FileOutputStream(mcPath + filename)) {
			byte[] dataBuffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// handle exception
			new JOptionPane("Failed to download an 'unsup' file. The rest of "
					+ "the install should be okay. Ask Thomas/Owen for help.",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
