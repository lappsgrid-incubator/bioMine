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
		description = "Index PubMed or PubMed Central.",
		optionListHeading = "OPTIONS",
		sortOptions = false,
		footer = "Copyright 2018 The Language Applications Grid"
)
public class Main implements Runnable
{
	@Option(names={"-p", "--path"},
			required = true,
			paramLabel = "PATH",
			description = "root directory of the corpus to index")
	private String path = null;

	@Option(names = "-n",
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
				System.out.println("Process has been interrupted.");
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
			if (entry.isFile() && entry.getName().endsWith(".nxml")) {
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

	public static void main(String[] args) {
		CommandLine.run(new Main(), args);
	}
}
