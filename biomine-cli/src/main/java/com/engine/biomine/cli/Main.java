package com.engine.biomine.cli;

import com.engine.biomine.indexing.IndexManager;
import com.engine.biomine.indexing.IndexerStatus;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

/**
 *
 */
@Command(name = "biomine-cli",
		description = "Recrsively scan a directory tree and index PubMed or PubMed Central articles found.",
		optionListHeading = "OPTIONS",
		sortOptions = false,
		footer = "Copyright 2018 The Language Applications Grid"
)
public class Main implements Runnable
{
	@Option(names={"-p", "--path"},
			required = true,
			paramLabel = "directory",
			description = "root directory of the corpus to index")
	private String path = null;

	@Option(names = {"-n", "--number"},
			paramLabel = "SIZE",
			description = "number of files to process")
	private int size = Integer.MAX_VALUE;

	private IndexManager indexer = new IndexManager();

	public Main()
	{

	}

	public void run() {
		File root = new File(path);
		if (!root.exists()) {
			System.out.println("That path does not exist.");
			return;
		}
		index(root);
		IndexerStatus status = indexer.getStatus();
		long n = status.getNbDocsToProcess();
		while (n > 0) {
			System.out.printf("There are %d documents to process.\n", n);
			try
			{
				Thread.sleep(30000);
				n = status.getNbDocsToProcess();
			}
			catch (InterruptedException e)
			{
				System.out.println("Processing has been interrupted.");
				// Signal the while loop to terminate.
				n = 0;
			}
		}
		System.out.println("Done.");
	}

	private void index(File root) {
		Deque<File> stack = new ArrayDeque<>();
		int count = 0;
		stack.push(root);
		while (!stack.isEmpty()) {
			File entry = stack.pop();
			if (accept(entry)) {
				indexer.pushData(entry, false, "literature");
				if (++count > size) {
					// User specified limit has been reached.
					return;
				}
			}
			else if (entry.isDirectory()) {
				for (File file : entry.listFiles()) {
					stack.push(file);
				}
			}
		}
	}

	private boolean accept(File file) {
		if (file.isFile()) {
			String name = file.getName();
			return name.endsWith(".xml") || name.endsWith(".nxml");
		}
		return false;
	}

	public static void main(String[] args) {
		CommandLine.run(new Main(), args);
	}
}
