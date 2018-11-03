/************************************************************************************************
 *  _________          _     ____          _           __        __    _ _      _   _   _ ___
 * |__  / ___|__ _ ___| |__ / ___|_      _(_)_ __   __ \ \      / /_ _| | | ___| |_| | | |_ _|
 *   / / |   / _` / __| '_ \\___ \ \ /\ / / | '_ \ / _` \ \ /\ / / _` | | |/ _ \ __| | | || |
 *  / /| |__| (_| \__ \ | | |___) \ V  V /| | | | | (_| |\ V  V / (_| | | |  __/ |_| |_| || |
 * /____\____\__,_|___/_| |_|____/ \_/\_/ |_|_| |_|\__, | \_/\_/ \__,_|_|_|\___|\__|\___/|___|
 *                                                 |___/
 *
 * Copyright (c) 2016 Ivan Vaklinov <ivan@vaklinov.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 **********************************************************************************/
package com.vaklinov.zerowallet;


import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.StringTokenizer;

import com.vaklinov.zerowallet.OSUtil.OS_TYPE;


/**
 * Observes the daemon - running etc.
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class ZeroInstallationObserver
{
	public static class DaemonInfo
	{
		public DAEMON_STATUS status;
		public double residentSizeMB;
		public double virtualSizeMB;
		public double cpuPercentage;
	}

	public static enum DAEMON_STATUS
	{
		RUNNING,
		NOT_RUNNING,
		UNABLE_TO_ASCERTAIN;
	}

	public ZeroInstallationObserver(String installDir)
		throws IOException
	{
		// Detect daemon and client tools installation
		File dir = new File(installDir);

		if (!dir.exists() || dir.isFile())
		{
			throw new InstallationDetectionException(
				"The Ƶero installation directory " + installDir + " does not exist or is not " +
			    "a directory or is otherwise inaccessible to the wallet!");
		}

		File zerod = new File(dir, OSUtil.getZerod());
		File zerocli = new File(dir, OSUtil.getZeroCli());

		if ((!zerod.exists()) || (!zerocli.exists()))
		{
			zerod = OSUtil.findZeroCommand(OSUtil.getZerod());
			zerocli = OSUtil.findZeroCommand(OSUtil.getZeroCli());
		}

		Log.info("Using Ƶero utilities: " +
		                   "zerod: "    + ((zerod != null) ? zerod.getCanonicalPath() : "<MISSING>") + ", " +
		                   "zero-cli: " + ((zerocli != null) ? zerocli.getCanonicalPath() : "<MISSING>"));

		if ((zerod == null) || (zerocli == null) || (!zerod.exists()) || (!zerocli.exists()))
		{
			throw new InstallationDetectionException(
				"The Ƶero GUI Wallet installation directory " + installDir + " needs\nto contain " +
				"the command line utilities zerod and zero-cli. At least one of them is missing! \n" +
				"Please place files ZeroSwingWalletUI.jar, " + OSUtil.getZeroCli() + ", " + 
				OSUtil.getZerod() + " in the same directory.");
		}
	}

	
	public synchronized DaemonInfo getDaemonInfo()
			throws IOException, InterruptedException
	{
		OS_TYPE os = OSUtil.getOSType();
		
		if (os == OS_TYPE.WINDOWS)
		{
			return getDaemonInfoForWindowsOS();
		} else
		{
			return getDaemonInfoForUNIXLikeOS();
		}
	}
	

	// So far tested on Mac OS X and Linux - expected to work on other UNIXes as well
	private synchronized DaemonInfo getDaemonInfoForUNIXLikeOS()
		throws IOException, InterruptedException
	{
		DaemonInfo info = new DaemonInfo();
		info.status = DAEMON_STATUS.UNABLE_TO_ASCERTAIN;

		CommandExecutor exec = new CommandExecutor(new String[] { "ps", "auxwww"});
		LineNumberReader lnr = new LineNumberReader(new StringReader(exec.execute()));

		String line;
		while ((line = lnr.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(line, " \t", false);
			boolean foundZero = false;
			for (int i = 0; i < 11; i++)
			{
				String token = null;
				if (st.hasMoreTokens())
				{
					token = st.nextToken();
				} else
				{
					break;
				}

				if (i == 2)
				{
					try
					{
						info.cpuPercentage = Double.valueOf(token);
					} catch (NumberFormatException nfe) { /* TODO: Log or handle exception */ };
				} else if (i == 4)
				{
					try
					{
						info.virtualSizeMB = Double.valueOf(token) / 1000;
					} catch (NumberFormatException nfe) { /* TODO: Log or handle exception */ };
				} else if (i == 5)
				{
					try
					{
					    info.residentSizeMB = Double.valueOf(token) / 1000;
					} catch (NumberFormatException nfe) { /* TODO: Log or handle exception */ };
				} else if (i == 10)
				{
					if ((token.equals("zerod")) || (token.endsWith("/zerod")))
					{
						info.status = DAEMON_STATUS.RUNNING;
						foundZero = true;
						break;
					}
				}
			}

			if (foundZero)
			{
				break;
			}
		}

		if (info.status != DAEMON_STATUS.RUNNING)
		{
			info.cpuPercentage  = 0;
			info.residentSizeMB = 0;
			info.virtualSizeMB  = 0;
		}

		return info;
	}
	
	
	private synchronized DaemonInfo getDaemonInfoForWindowsOS()
		throws IOException, InterruptedException
	{
		DaemonInfo info = new DaemonInfo();
		info.status = DAEMON_STATUS.UNABLE_TO_ASCERTAIN;
		info.cpuPercentage = 0;
		info.virtualSizeMB = 0;

		CommandExecutor exec = new CommandExecutor(new String[] { "tasklist" });
		LineNumberReader lnr = new LineNumberReader(new StringReader(exec.execute()));

		String line;
		while ((line = lnr.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(line, " \t", false);
			boolean foundZero = false;
			String size = "";
			for (int i = 0; i < 8; i++)
			{
				String token = null;
				if (st.hasMoreTokens())
				{
					token = st.nextToken();
				} else
				{
					break;
				}
				
				if (token.startsWith("\""))
				{
					token = token.substring(1);
				}
				
				if (token.endsWith("\""))
				{
					token = token.substring(0, token.length() - 1);
				}

				if (i == 0)
				{
					if (token.equals("zerod.exe") || token.equals("zerod"))
					{
						info.status = DAEMON_STATUS.RUNNING;
						foundZero = true;
						//System.out.println("Zerod process data is: " + line);
					}
				} else if ((i >= 4) && foundZero)
				{
					try
					{
						size += token.replaceAll("[^0-9]", "");
						if (size.endsWith("K"))
						{
							size = size.substring(0, size.length() - 1);
						}
					} catch (NumberFormatException nfe) { /* TODO: Log or handle exception */ };
				} 
			} // End parsing row

			if (foundZero)
			{
				try
				{
					info.residentSizeMB = Double.valueOf(size) / 1000;
				} catch (NumberFormatException nfe)
				{
					info.residentSizeMB = 0;
					Log.error("Error: could not find the numeric memory size of zerod: " + size);
				};
				
				break;
			}
		}

		if (info.status != DAEMON_STATUS.RUNNING)
		{
			info.cpuPercentage  = 0;
			info.residentSizeMB = 0;
			info.virtualSizeMB  = 0;
		}

		return info;
	}
	

	public static class InstallationDetectionException
		extends IOException
	{
		public InstallationDetectionException(String message)
		{
			super(message);
		}
	}
}
