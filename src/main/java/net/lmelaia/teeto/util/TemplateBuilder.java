package net.lmelaia.teeto.util;

import net.lmelaia.teeto.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Gets the text of a .template file, and allows the replacing of
 * placeholders in the template file with strings.
 */
public class TemplateBuilder {

    /**
     * The .template file.
     */
    private final File templateFile;

    /**
     * The .template file text.
     */
    private String template;

    /**
     * Constructs a new template builder.
     *
     * @param template the name of the .template file, excluding the extension and path.
     * @throws IOException if the .template file cannot be found.
     */
    public TemplateBuilder(String template) throws IOException {
        this.templateFile = new File(Constants.getTemplateFolder() + "/" + template + ".template");
        this.template = readTemplateFile();
    }

    /**
     * Replaces a placeholder in the templates text with a given String.
     *
     * @param placeholder the placeholder.
     * @param replacement the String to replace the placeholder with.
     * @return {@code this}.
     */
    public TemplateBuilder setPlaceholder(String placeholder, String replacement){
        template = template.replace(placeholder, replacement);
        return this;
    }

    /**
     * @return the template text with all set placeholders.
     */
    public String build(){
        return template;
    }

    /**
     * Reads the .template file.
     *
     * @return the .template file text.
     * @throws IOException if the file cannot be read.
     */
    private String readTemplateFile() throws IOException {
        return FileUtil.readFile(templateFile);
    }
}
