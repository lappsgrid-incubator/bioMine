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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 */
@Command(name = "biomine-cli",
		description = "Recrsively scan a directory tree and index the Pubmed or PubmedCentral articles found.",
		optionListHeading = "OPTIONS\n",
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

	@Option(names = {"-v", "--version"},
			paramLabel = "VERSION",
			versionHelp = true,
			description = "print program version and exit")
	private boolean showVersion;

	@Option(names = {"-h", "--help"},
			paramLabel = "HELP",
			usageHelp = true,
			description = "print this help message.")
	private boolean showHelp;

	private IndexManager indexer; // = new IndexManager();

	public Main()
	{

	}

	public void run() {
		File root = new File(path);
		if (!root.exists()) {
			System.out.println("That path does not exist.");
			return;
		}
		indexer = new IndexManager();
		List<Future<Boolean>> tasks = index(root);
		try {
			for (Future<Boolean> task : tasks) {
				task.get();
			}
		}
		catch (InterruptedException e) {
			System.out.println("Task was interrupted.");
			Thread.currentThread().interrupt();;
		}
		catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		finally {
			System.out.println("Done.");
		}
//		IndexerStatus status = indexer.getStatus();
//		long n = status.getNbDocsToProcess();
//		while (n > 0) {
//			System.out.printf("There are %d documents to process.\n", n);
//			try
//			{
//				Thread.sleep(30000);
//				n = status.getNbDocsToProcess();
//			}
//			catch (InterruptedException e)
//			{
//				System.out.println("Processing has been interrupted.");
//				// Signal the while loop to terminate.
//				n = 0;
//			}
//		}
	}

	private List<Future<Boolean>> index(File root) {
		Deque<File> stack = new ArrayDeque<>();
		stack.push(root);
		List<Future<Boolean>> tasks = new ArrayList<>();
		while (!stack.isEmpty()) {
			File entry = stack.pop();
			if (accept(entry)) {
				System.out.println("Indexing " + entry.getPath());
				List<Future<Boolean>> futures = indexer.pushData(entry, false, "literature");
				tasks.addAll(futures);
				if (tasks.size() > size) {
					// User specified limit has been reached.
					return tasks;
				}
			}
			else if (entry.isDirectory()) {
				for (File file : entry.listFiles()) {
					stack.push(file);
				}
			}
		}
		return tasks;
	}

	private boolean accept(File file) {
		if (file.isFile()) {
			String name = file.getName();
			return name.endsWith(".xml") || name.endsWith(".nxml");
		}
		return false;
	}

	public static void main(String[] args) {

//		CommandLine.run(new Main(), args);
		Main app = new Main();
		CommandLine command = null;
		try {
			command = new CommandLine(app);
			command.parse(args);
		}
		catch (CommandLine.ParameterException e) {
			System.out.println(e.getMessage());
			command.usage(System.out);
			return;
		}

		if (command.isVersionHelpRequested()) {
			System.out.println("bioMine CLI Indexer v" + Version.getVersion());
			System.out.printf("Copyright %c 2018 The Lanugage Applications Grid\n\n", 169);
			return;
		}
		if (command.isUsageHelpRequested()) {
			command.usage(System.out);
			return;
		}
		app.run();
	}
}
