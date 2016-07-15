package orderprocessing;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;
import javax.xml.transform.stream.StreamSource;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static byte[] messageIn = readInputMessage();

    public static void main( String[] args )
    {

        System.out.println("\n\n==============Message In==============");
        System.out.println(new String(messageIn));
        System.out.println("======================================\n");

        try {
                    String messageOut = runSmooksTransform();
                    //save to xml file
                    try {
                        File file = new File("src/output/850_Order_Original.xml");
                        FileOutputStream fileOut = new FileOutputStream(file);
                        PrintStream out = new PrintStream(fileOut);
                        out.print(messageOut);
                        out.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("==============Message Out=============");
                    System.out.println(messageOut);
                    System.out.println("======================================\n\n");

        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    protected static String runSmooksTransform() throws IOException, SAXException, SmooksException {

        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("en", "IE"));

        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks("src/config/smooks-850EdiToXml.xml");
        try {
            // Create an exec context - no profiles....
            ExecutionContext executionContext = smooks.createExecutionContext();

            StringResult result = new StringResult();



            // Filter the input message to the outputWriter, using the execution context...
            smooks.filterSource(executionContext, new StreamSource(new ByteArrayInputStream(messageIn)), result);
            Locale.setDefault(defaultLocale);

            return result.getResult();
        } finally {
            smooks.close();
        }
    }

    private static byte[] readInputMessage() {
        try {
            return StreamUtils.readStream(new FileInputStream("src/input/Order-850.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }
}
