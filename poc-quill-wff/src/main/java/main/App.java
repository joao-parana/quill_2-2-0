package main;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.val.Readable;
import org.tomitribe.util.IO;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class App {

    @Command
    public void mpj(@Option("dataset") @Default("my-dataset") final String dataset) 
            throws FileNotFoundException, IOException   {
        String errMsg = "O único dataset suportado no Momento é o ... " +
                    "\n...";
        String msg = String.format("\nProcessando Varredura de Parâmetros no dataset'%s'\n\n", dataset);
        System.out.println(msg);

        App.doIt(dataset);
    }

    public static void doIt(final String dataset) {
        System.out.println("TODO: Implementar");
    }
}