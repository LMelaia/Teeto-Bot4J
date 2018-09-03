/*
 *  This file is part of TeetoBot4J.
 *
 *  TeetoBot4J is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TeetoBot4J is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TeetoBot4J.  If not, see <https://www.gnu.org/licenses/>.
 */
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
