package org.ultramine.server.world.imprt;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DirectoryChunkLoader extends ImportChunkLoader
{
	private final File fromDir;
	
	public DirectoryChunkLoader(File tempDir, File fromDir)
	{
		super(tempDir);
		this.fromDir = fromDir;
	}

	@Override
	protected void unpackFile(String name) throws IOException
	{
		FileUtils.copyFile(new File(fromDir, name), new File(tempDir, name));
	}
}
